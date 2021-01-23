package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.CmBean;
import org.pra.nse.csv.read.CmCsvReader;
import org.pra.nse.db.dao.CmDao;
import org.pra.nse.db.model.NseCashMarketTab;
import org.pra.nse.db.repository.NseCmRepo;
import org.pra.nse.db.upload.BaseUploader;
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
public class NseCmUploader extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseCmUploader.class);

    private final NseCmRepo repository;
    private final CmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final CmCsvReader csvReader;

    public NseCmUploader(NseCmRepo nseCmRepo,
                         CmDao cmDao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         CmCsvReader cmCsvReader ) {
        super(praFileUtils, NseCons.CM_DIR_NAME, ApCo.PRA_CM_FILE_PREFIX);
        this.repository = nseCmRepo;
        this.dao = cmDao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = cmCsvReader;
    }


    public void uploadForDate(LocalDate forDate) {
        //TODO check that number of rows in file and number of rows in table matches for the given date
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("CM-upload | SKIPPING - already uploaded | for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("CM-upload | uploading | for date:[{}]", forDate);
        }

        String fromFile = NseCons.CM_FILES_PATH + File.separator+ ApCo.PRA_CM_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("CM-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFileExist(fromFile)) {
            LOGGER.warn("CM-upload | file does not exist: [{}]", fromFile);
            return;
        }
        Map<String, CmBean> latestBeanMap = csvReader.read(fromFile);

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
            try {
                //TODO batch insert for efficiency
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("CM-upload | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
    }

}

