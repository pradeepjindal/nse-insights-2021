package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.NxBean;
import org.pra.nse.csv.read.NxCsvReader;
import org.pra.nse.db.dao.NseNxDao;
import org.pra.nse.db.model.NseIndexTab;
import org.pra.nse.db.repository.NseNxRepo;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NseNxUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseNxUploader.class);

    private final LocalDate NSE_IDX_FILE_AVAILABLE_FROM_DATE = LocalDate.of(2020, 1, 1);
    private final NseNxRepo repository;
    private final NseNxDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final NxCsvReader csvReader;

    private String fileDirName = PraCons.NX_DIR_NAME;
    private String filePrefix = PraCons.PRA_NX_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + PraCons.NX_DIR_NAME;

    public NseNxUploader(NseNxRepo repository,
                         NseNxDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         NxCsvReader csvReader ) {
        this.repository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_NX_FILE_AVAILABLE_FROM_DATE);
    }
    public void upload2021() {
        uploadFromDate(LocalDate.of(2021, 1, 1));
    }
    public void uploadFromDefaultDate() {
        LocalDate uploadFromDate;
        if(ApCo.UPLOAD_NSE_FROM_DATE.isBefore(NSE_IDX_FILE_AVAILABLE_FROM_DATE)) uploadFromDate = NSE_IDX_FILE_AVAILABLE_FROM_DATE;
        else uploadFromDate = ApCo.UPLOAD_NSE_FROM_DATE;
        uploadFromDate(uploadFromDate);
    }
//    public void uploadFromLatestDate() {
//        LocalDate dt = UploadHelper.getLatestFileDate(praFileUtils, NseCons.IDX_DIR_NAME, ApCo.PRA_IDX_FILE_PREFIX);
//        looper(dt);
//    }
    public void uploadFromLatestDate() {
        //String dataDir = ApCo.ROOT_DIR + File.separator + fileDirName;
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, filePrefix, ApCo.DATA_FILE_EXT, 1);
        LocalDate dt = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);
        looper(dt);
    }
    public void uploadFromDate(LocalDate fromDate) {
        looper(fromDate);
    }

    private void looper(LocalDate fromDate) {
        LocalDate today = LocalDate.now();
        LocalDate processingDate = fromDate.minusDays(1);
        do {
            processingDate = processingDate.plusDays(1);
            //LOGGER.info("nx | upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
            if(DateUtils.isTradingOnHoliday(processingDate)) {
                uploadForDate(processingDate);
            } else if (DateUtils.isWeekend(processingDate)) {
                continue;
            } else {
                uploadForDate(processingDate);
            }
        } while(today.compareTo(processingDate) > 0);
    }

    public void uploadForDate(LocalDate forDate) {
        if(DateUtils.notTradingDay(forDate)) return;

        //TODO check that number of rows in file and number of rows in table matches for the given date
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("nx | already uploaded - for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("nx | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator+ PraCons.PRA_NX_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("IDX-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("nx | file not found: [{}]", fromFile);
            return;
        }

        Map<String, NxBean> latestBeanMap = csvReader.read(fromFile);
        upload(forDate, latestBeanMap);
    }


    private void upload(LocalDate forDate, Map<String, NxBean> latestBeanMap) {
        NseIndexTab target = new NseIndexTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();
        latestBeanMap.values().forEach( source -> {
            target.reset();
            target.setSymbol(source.getSymbol());
            target.setIdxName(source.getIdxName());
            target.setTradeDate(DateUtils.toLocalDate(source.getTradeDate()));
            target.setOpen(source.getOpen());
            target.setHigh(source.getHigh());
            target.setLow(source.getLow());
            target.setClose(source.getClose());

            target.setPointsChgAbs(source.getPointsChgAbs());
            target.setPointsChgPct(source.getPointsChgPct());
            target.setVolume(source.getVolume());
            target.setTurnOverInCrore(source.getTurnOverInCrore());
            target.setPe(source.getPe());
            target.setPb(source.getPb());
            target.setDivYield(source.getDivYield());
            //
            target.setTds(forDate.toString());
            target.setTdn(Integer.valueOf(forDate.toString().replace("-", "")));
            try {
                //TODO batch insert for efficiency
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("nx | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("nx | some record could not be persisted");
    }

}

