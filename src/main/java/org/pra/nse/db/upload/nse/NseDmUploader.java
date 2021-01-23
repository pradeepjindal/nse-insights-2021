package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.DmBean;
import org.pra.nse.csv.read.DmCsvReader;
import org.pra.nse.db.dao.DmDao;
import org.pra.nse.db.model.NseDeliveryMarketTab;
import org.pra.nse.db.repository.NseDmRepo;
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
public class NseDmUploader extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseDmUploader.class);

    private final NseDmRepo repository;
    private final DmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final DmCsvReader csvReader;

    public NseDmUploader(NseDmRepo nseDmRepo,
                         DmDao dmDao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         DmCsvReader csvReader) {
        super(praFileUtils, NseCons.DM_DIR_NAME, ApCo.PRA_DM_FILE_PREFIX);
        this.repository = nseDmRepo;
        this.dao = dmDao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }


    public void uploadForDate(LocalDate forDate) {
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("DM-upload | SKIPPING - already uploaded | for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("DM-upload | uploading | for date:[{}]", forDate);
        }

        String fromFile = NseCons.DM_FILES_PATH + File.separator+ ApCo.PRA_DM_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("DM-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFileExist(fromFile)) {
            LOGGER.warn("DM-upload | file does not exist: [{}]", fromFile);
            return;
        }
        Map<String, DmBean> mtLatestBeanMap = csvReader.read(fromFile);

        NseDeliveryMarketTab target = new NseDeliveryMarketTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();
        mtLatestBeanMap.values().forEach( source-> {
            target.reset();
            target.setSymbol(source.getSymbol());
            target.setSecurityType(source.getSecurityType());
            target.setTradedQty(source.getTradedQty());
            target.setDeliverableQty(source.getDeliverableQty());
            target.setDeliveryToTradeRatio(source.getDeliveryToTradeRatio());
            target.setTradeDate(DateUtils.toLocalDate(source.getTradeDate()));
            try {
                repository.save(target);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("DM-upload | record - uploaded {}, failed: [{}]", recordSucceed.get(), recordFailed.get());
    }

}
