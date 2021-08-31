package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.IdxBean;
import org.pra.nse.csv.read.IdxCsvReader;
import org.pra.nse.db.dao.IdxDao;
import org.pra.nse.db.model.NseIndexMarketTab;
import org.pra.nse.db.repository.NseIdxRepo;
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
public class NseIdxUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseIdxUploader.class);

    private final LocalDate NSE_IDX_FILE_AVAILABLE_FROM_DATE = LocalDate.of(2020, 1, 1);
    private final NseIdxRepo repository;
    private final IdxDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final IdxCsvReader csvReader;

    private String fileDirName = ApCo.IDX_DIR_NAME;
    private String filePrefix = ApCo.PRA_IDX_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + ApCo.IDX_DIR_NAME;

    public NseIdxUploader(NseIdxRepo repository,
                          IdxDao dao,
                          NseFileUtils nseFileUtils,
                          PraFileUtils praFileUtils,
                          IdxCsvReader csvReader ) {
        this.repository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_IDX_FILE_AVAILABLE_FROM_DATE);
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
            LOGGER.info("IDX-upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
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
        //TODO check that number of rows in file and number of rows in table matches for the given date
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("IDX-upload | already uploaded | for date:[{}]", forDate);
            return;
        } else {
//            LOGGER.info("IDX-upload | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator+ ApCo.PRA_IDX_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("IDX-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("IDX-upload | file not found: [{}]", fromFile);
            return;
        }

        Map<String, IdxBean> latestBeanMap = csvReader.read(fromFile);
        upload(latestBeanMap);
    }


    private void upload(Map<String, IdxBean> latestBeanMap) {
        NseIndexMarketTab target = new NseIndexMarketTab();
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
            try {
                //TODO batch insert for efficiency
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("IDX-upload | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("IDX-upload | some record could not be persisted");
    }

}

