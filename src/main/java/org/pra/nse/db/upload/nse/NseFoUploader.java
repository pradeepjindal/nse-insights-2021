package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.FoBean;
import org.pra.nse.csv.read.FoCsvReader;
import org.pra.nse.db.dao.NseFoDao;
import org.pra.nse.db.model.NseFoTab;
import org.pra.nse.db.repository.NseFoRepo;
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
public class NseFoUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseFoUploader.class);

    private final LocalDate NSE_FO_FILE_AVAILABLE_FROM_DATE = LocalDate.of(2016, 1, 1);
    private final NseFoRepo repository;
    private final NseFoDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final FoCsvReader csvReader;

    private String fileDirName = ApCo.FO_DIR_NAME;
    private String filePrefix = ApCo.PRA_FO_FILE_PREFIX;
    private String fileExtension = ApCo.DATA_FILE_EXT;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + ApCo.FO_DIR_NAME;

    public NseFoUploader(FoCsvReader csvReader,
                         NseFoRepo repository,
                         NseFoDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils
                          ) {
        this.csvReader = csvReader;
        this.repository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_FO_FILE_AVAILABLE_FROM_DATE);
    }
    public void upload2021() {
        uploadFromDate(LocalDate.of(2021, 1, 1));
    }
    public void uploadFromDefaultDate() {
        LocalDate uploadFromDate;
        if(ApCo.UPLOAD_NSE_FROM_DATE.isBefore(NSE_FO_FILE_AVAILABLE_FROM_DATE)) uploadFromDate = NSE_FO_FILE_AVAILABLE_FROM_DATE;
        else uploadFromDate = ApCo.UPLOAD_NSE_FROM_DATE;
        uploadFromDate(uploadFromDate);
    }
//    public void uploadFromLatestDate() {
//        LocalDate dt = UploadHelper.getLatestFileDate(praFileUtils, NseCons.IDX_DIR_NAME, ApCo.PRA_IDX_FILE_PREFIX);
//        looper(dt);
//    }
    public void uploadFromLatestDate() {
        //String dataDir = ApCo.ROOT_DIR + File.separator + fileDirName;
        String latestFileName = praFileUtils.getLatestFileNameFor(Data_Dir, filePrefix, fileExtension, 1);
        LocalDate latestFileDate = latestFileName == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(latestFileName);
        looper(latestFileDate);
    }
    public void uploadFromDate(LocalDate fromDate) {
        looper(fromDate);
    }

    private void looper(LocalDate fromDate) {
        LocalDate today = LocalDate.now();
        LocalDate processingDate = fromDate.minusDays(1);
        do {
            processingDate = processingDate.plusDays(1);
            LOGGER.info("nx- | upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
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
            LOGGER.info("nx- | upload | already uploaded | for date:[{}]", forDate);
            return;
        } else {
//            LOGGER.info("IDX-upload | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator + filePrefix + forDate + fileExtension;
        //LOGGER.info("IDX-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("fo- | upload | file not found: [{}]", fromFile);
            return;
        }

        Map<String, Map<LocalDate, FoBean>> beanMap = csvReader.read(fromFile);
        upload(forDate, beanMap);
    }


    private void upload(LocalDate forDate, Map<String, Map<LocalDate, FoBean>> beanMap) {
        NseFoTab target = new NseFoTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();
        beanMap.values().forEach( source -> {
            target.reset();
//            target.setSymbol(source.getSymbol());
//            target.setTradeDate(source.getExpiryDate());

            //
            target.setTdn(Integer.valueOf(forDate.toString().replace("-", "")));
            try {
                //TODO batch insert for efficiency
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("fo- | upload | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("FO-upload | some record could not be persisted");
    }

}

