package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.LsBean;
import org.pra.nse.csv.read.LsCsvReader;
import org.pra.nse.db.dao.NseLsDao;
import org.pra.nse.db.model.NseLotSizeTab;
import org.pra.nse.db.repository.NseLsRepo;
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
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NseLsUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseLsUploader.class);

    private final NseLsRepo repository;
    private final LsCsvReader csvReader;
    private final NseLsDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;


    private final String fileDirName = PraCons.LS_DIR_NAME;
    private final String filePrefix = PraCons.PRA_LS_FILE_PREFIX;
    private final String fileExtension = ApCo.DATA_FILE_EXT;
    private final LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + fileDirName;

    public NseLsUploader(NseLsRepo repository,
                         LsCsvReader csvReader,
                         NseLsDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils) {
        this.repository = repository;
        this.csvReader = csvReader;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_LS_FILE_AVAILABLE_FROM_DATE);
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
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, filePrefix, fileExtension, 1);
        LocalDate dt = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);
        looper(dt);
    }

    private void looper(LocalDate fromDate) {
        LocalDate today = LocalDate.now();
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
        } while(today.compareTo(processingDate) > 0);
    }

    public void uploadForDate(LocalDate forDate) {
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("ls | already uploaded - for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("ls | uploading - for date:[{}]", forDate);
        }

        LocalDate latestFileDate_forDate;
        {
//            LocalDate abc = LocalDate.of(2021, 12, 2);
            String str = praFileUtils.getLatestFileNameFor(Data_Dir, filePrefix, fileExtension, 1, forDate);
            latestFileDate_forDate = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);
        }

        String fromFile = Data_Dir + File.separator + PraCons.PRA_LS_FILE_PREFIX + latestFileDate_forDate + ApCo.DATA_FILE_EXT;
        //LOGGER.info("LS-upload | looking for file Name along with path:[{}]",fromFile);

        if(nseFileUtils.isFileAbsent(fromFile)) {
            LOGGER.warn("ls | file not found: [{}]", fromFile);
            return;
        }

        LocalDate fix_expiry_date = LocalDate.of(latestFileDate_forDate.getYear(), latestFileDate_forDate.getMonthValue(), 1);
        LOGGER.info("ls | uploading - forDate [{}], fileDate: [{}]", forDate, latestFileDate_forDate);

        List<LsBean> beans = csvReader.read(fromFile);
        //LOGGER.info("{}", foBeanMap.size());
        upload(forDate, beans, latestFileDate_forDate);
    }


    private void upload(LocalDate forDate, List<LsBean> beans, LocalDate fileDate) {
        NseLotSizeTab target = new NseLotSizeTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordSkipped = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();

        for(LsBean bean: beans) {
            try {
                if(bean.getLotSize() == null) continue;

                target.reset();
                target.setSymbol(bean.getSymbol());

                target.setTradeDate(forDate);
                target.setTdn(Integer.valueOf(forDate.toString().replace("-", "")));

                target.setExpiryDate(bean.getExpiryDate());
                target.setEdn(Integer.valueOf(bean.getExpiryDate().toString().replace("-", "")));

                target.setLotSize(bean.getLotSize());

                target.setFileDate(fileDate);
                target.setFdn(Integer.valueOf(fileDate.toString().replace("-", "")));

                repository.save(target);
                recordSucceed.incrementAndGet();
            }catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        }

        LOGGER.info("ls | record - uploaded {}, skipped {}, failed: [{}]", recordSucceed.get(), recordSkipped.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("ls | some record could not be persisted");
    }

}
