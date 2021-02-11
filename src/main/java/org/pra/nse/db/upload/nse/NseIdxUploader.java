package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.IdxBean;
import org.pra.nse.csv.read.IdxCsvReader;
import org.pra.nse.db.dao.IdxDao;
import org.pra.nse.db.model.NseIndexMarketTab;
import org.pra.nse.db.repository.NseIdxRepo;
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
public class NseIdxUploader extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseIdxUploader.class);

    private final NseIdxRepo repository;
    private final IdxDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final IdxCsvReader csvReader;

    public NseIdxUploader(NseIdxRepo repository,
                          IdxDao dao,
                          NseFileUtils nseFileUtils,
                          PraFileUtils praFileUtils,
                          IdxCsvReader csvReader ) {
        super(praFileUtils, NseCons.IDX_DIR_NAME, ApCo.PRA_IDX_FILE_PREFIX);
        this.repository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }


    public void uploadForDate(LocalDate forDate) {
        //TODO check that number of rows in file and number of rows in table matches for the given date
        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("IDX-upload | SKIPPING - already uploaded | for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("IDX-upload | uploading | for date:[{}]", forDate);
        }

        String fromFile = NseCons.IDX_FILES_PATH + File.separator+ ApCo.PRA_IDX_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("IDX-upload | looking for file Name along with path:[{}]",fromFile);

        if(!nseFileUtils.isFileExist(fromFile)) {
            LOGGER.warn("IDX-upload | file does not exist: [{}]", fromFile);
            return;
        }
        Map<String, IdxBean> latestBeanMap = csvReader.read(fromFile);

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

