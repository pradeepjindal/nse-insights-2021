package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.PraCons;
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
import java.util.List;
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

    private String fileDirName = PraCons.FO_DIR_NAME;
    private String filePrefix = PraCons.PRA_FO_FILE_PREFIX;
    private String fileExtension = ApCo.DATA_FILE_EXT;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + PraCons.FO_DIR_NAME;

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
            //LOGGER.info("fo | upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
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
        //if(DateUtils.toInt(forDate) > 20211201) return;

        //TODO check that number of rows in file and number of rows in table matches for the given date
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("fo | already uploaded - for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("fo | uploading - for date:[{}]", forDate);
        }

        LocalDate fileDate;
        // special condition: nse file is corrupt for 23-Sep-2021 hence uploading the 22-Sep-2021
        if(forDate.toString().equals("2021-11-23")) {
            LOGGER.warn("fo | upload - special condition: nse file is corrupt for 23-Sep-2021 hence replacing it with 22-Sep-2021");
            fileDate = LocalDate.of(2021, 11, 22);
        } else {
            fileDate = forDate;
        }

        String fromFile = Data_Dir + File.separator + filePrefix + fileDate + fileExtension;
        //LOGGER.info("IDX-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFilePresent(fromFile)) {
            LOGGER.warn("fo | file not found: [{}]", fromFile);
            return;
        }

        List<FoBean> beanList = csvReader.read(fromFile);
        if(beanList.size() == 0) {
            String errorString = "fo | error - no data returned from csvReader forDate: " + forDate;
            LOGGER.error(errorString);
            throw new RuntimeException(errorString);
        } else {
            upload(forDate, beanList);
        }

    }


    private void upload(LocalDate forDate, List<FoBean> beanList) {
        NseFoTab target = new NseFoTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();
        beanList.forEach( bean -> {
            target.reset();
            target.setSymbol(bean.getSymbol());
            target.setTradeDate(forDate);
            target.setExpiryDate(bean.getExpiryDate());
            target.setInstrument(bean.getInstrument());
            target.setTurnover(bean.getTurnover());
            target.setQuantity(bean.getQuantity());
            target.setContracts(bean.getContracts());
            target.setLotSize(bean.getLotSize());
            target.setFileDate(forDate);
            //
            target.setTdn(Integer.valueOf(forDate.toString().replace("-", "")));
            target.setEdn(Integer.valueOf(bean.getExpiryDate().toString().replace("-", "")));
            target.setFdn(Integer.valueOf(forDate.toString().replace("-", "")));

            try {
                //TODO batch insert for efficiency
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("fo | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("fm | some record could not be persisted");
    }

}

