package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
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

    private String fileDirName = ApCo.DM_DIR_NAME;
    private String filePrefix = ApCo.PRA_DM_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + ApCo.DM_DIR_NAME;

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
    public void upload2021() {
        uploadFromDate(LocalDate.of(2021, 1, 1));
    }
    public void uploadFromDefaultDate() {
        uploadFromDate(defaultDate);
    }
    public void uploadFromDate(LocalDate fromDate) {
        looper(fromDate);
    }

    public void uploadFromLatestDate() {
        //String dataDir = ApCo.ROOT_DIR + File.separator + fileDirName;
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, filePrefix, ApCo.DATA_FILE_EXT, 1);
        LocalDate dt = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);
        looper(dt);
    }

    private void looper(LocalDate fromDate) {
        LocalDate today = LocalDate.now();
        LocalDate processingDate = fromDate.minusDays(1);
        do {
            processingDate = processingDate.plusDays(1);
            LOGGER.info("{} | upload processing date: [{}], {}", filePrefix, processingDate, processingDate.getDayOfWeek());
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
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("DM-upload | already uploaded | for date:[{}]", forDate);
            return;
        } else {
//            LOGGER.info("DM-upload | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator+ ApCo.PRA_DM_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("DM-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("DM-upload | file not found: [{}]", fromFile);
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
        LOGGER.info("DM-upload | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("DM-upload | some record could not be persisted");
    }
}
