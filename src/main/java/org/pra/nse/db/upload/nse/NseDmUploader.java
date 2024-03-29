package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.DmBean;
import org.pra.nse.csv.read.DmCsvReader;
import org.pra.nse.db.dao.NseDmDao;
import org.pra.nse.db.model.NseDeliveryMarketTab;
import org.pra.nse.db.repository.NseDmRepo;
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
public class NseDmUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseDmUploader.class);

    private final NseDmRepo repository;
    private final NseDmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final DmCsvReader csvReader;

    private String fileDirName = PraCons.DM_DIR_NAME;
    private String filePrefix = PraCons.PRA_DM_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + PraCons.DM_DIR_NAME;

    public NseDmUploader(NseDmRepo repository,
                         NseDmDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         DmCsvReader csvReader) {
        this.repository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_DM_FILE_AVAILABLE_FROM_DATE);
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

        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("dm | already uploaded - for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("dm | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator + PraCons.PRA_DM_FILE_PREFIX + forDate + ApCo.DATA_FILE_EXT;
        //LOGGER.info("DM-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("dm | file not found: [{}]", fromFile);
            return;
        }

        Map<String, DmBean> latestBeanMap = csvReader.read(fromFile);
        upload(forDate, latestBeanMap);
    }


    private void upload(LocalDate forDate, Map<String, DmBean> latestBeanMap) {
        NseDeliveryMarketTab target = new NseDeliveryMarketTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();
        latestBeanMap.values().forEach( source-> {
            target.reset();
            target.setSymbol(source.getSymbol());
            target.setSecurityType(source.getSecurityType());
            target.setTradedQty(source.getTradedQty());
            target.setDeliverableQty(source.getDeliverableQty());
            target.setDeliveryToTradeRatio(source.getDeliveryToTradeRatio());
            target.setTradeDate(DateUtils.toLocalDate(source.getTradeDate()));
            //
            target.setTds(forDate.toString());
            target.setTdn(Integer.valueOf(forDate.toString().replace("-", "")));
            try {
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("dm | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("dm | some record could not be persisted");
    }
}
