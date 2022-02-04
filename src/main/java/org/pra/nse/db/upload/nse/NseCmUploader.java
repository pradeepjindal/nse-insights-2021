package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.CmBean;
import org.pra.nse.csv.read.CmCsvReader;
import org.pra.nse.db.dao.NseCmDao;
import org.pra.nse.db.model.NseCashMarketTab;
import org.pra.nse.db.repository.NseCmRepo;
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
public class NseCmUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseCmUploader.class);

    private final NseCmRepo repository;
    private final NseCmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final CmCsvReader csvReader;

    private String fileDirName = PraCons.CM_DIR_NAME;
    private String filePrefix = PraCons.PRA_CM_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + PraCons.CM_DIR_NAME;

    public NseCmUploader(NseCmRepo repository,
                         NseCmDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         CmCsvReader cmCsvReader ) {
        this.repository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = cmCsvReader;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_CM_FILE_AVAILABLE_FROM_DATE);
    }
    public void upload2020() {
        looper(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
    }
    public void upload2021() {
        looper(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31));
    }

    public void uploadFromDefaultDate() {
        uploadFromDate(defaultDate);
    }
    public void uploadFromDate(LocalDate fromDate) {
        looper(fromDate, null);
    }

    public void uploadFromLatestDate() {
        //String dataDir = ApCo.ROOT_DIR + File.separator + fileDirName;
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, filePrefix, ApCo.DATA_FILE_EXT, 1);
        LocalDate dt = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);
        looper(dt, null);
    }

    private void looper(LocalDate fromDate, LocalDate toDate) {
        if(toDate == null) toDate = LocalDate.now();
        LocalDate processingDate = fromDate.minusDays(1);
        do {
            processingDate = processingDate.plusDays(1);
            //LOGGER.info("{} | upload processing date: [{}], {}", filePrefix, processingDate, processingDate.getDayOfWeek());
            if(DateUtils.isTradingOnHoliday(processingDate)) {
                uploadForDate(processingDate);
            } else if (DateUtils.isWeekend(processingDate)) {
                continue;
            } else {
                uploadForDate(processingDate);
            }
        } while(toDate.compareTo(processingDate) > 0);
    }

    public void uploadForDate(LocalDate forDate) {
        if(DateUtils.notTradingDay(forDate)) return;

        //TODO check that number of rows in file and number of rows in table matches for the given date
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("cm | already uploaded - for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("cm | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator + PraCons.PRA_CM_FILE_PREFIX + forDate + ApCo.DATA_FILE_EXT;
        //LOGGER.info("CM-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("cm | file not found: [{}]", fromFile);
            return;
        }

        Map<String, CmBean> latestBeanMap = csvReader.read(fromFile);
        upload(forDate, latestBeanMap);
    }

    private void upload(LocalDate forDate, Map<String, CmBean> latestBeanMap) {
        NseCashMarketTab target = new NseCashMarketTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();
        latestBeanMap.values().forEach( source -> {
            target.reset();
            target.setSymbol(source.getSymbol());
            target.setSeries(source.getSeries());
            target.setOpen(source.getOpen());
            target.setHigh(source.getHigh());
            target.setLow(source.getLow());
            target.setClose(source.getClose());
            target.setLast(source.getLast());
            target.setPrevClose(source.getPrevClose());
            target.setTotTrdQty(source.getTotTrdQty());
            target.setTotTrdVal(source.getTotTrdVal());
            target.setTradeDate(DateUtils.toLocalDate(source.getTimestamp()));
            target.setTotalTrades(source.getTotalTrades());
            target.setIsin(source.getIsin());
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
        LOGGER.info("cm | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("cm | some record could not be persisted");
    }

}

