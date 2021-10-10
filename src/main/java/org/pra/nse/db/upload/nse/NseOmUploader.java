package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.FmBean;
import org.pra.nse.csv.read.FmCsvReader;
import org.pra.nse.csv.read.OmCsvReader;
import org.pra.nse.db.dao.OmDao;
import org.pra.nse.db.model.NseOptionMarketTab;
import org.pra.nse.db.repository.NseOmRepo;
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
public class NseOmUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseOmUploader.class);

    private final NseOmRepo optionMarketRepository;
    private final OmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final OmCsvReader csvReader;

    private String fileDirName = ApCo.FM_DIR_NAME;
    private String filePrefix = ApCo.PRA_FM_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + ApCo.FM_DIR_NAME;

    public NseOmUploader(NseOmRepo repository,
                         OmDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         OmCsvReader omCsvReader) {
        this.optionMarketRepository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = omCsvReader;
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
            LOGGER.info("OM-upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
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
            LOGGER.info("OM-upload | already uploaded | for date:[{}]", forDate);
            return;
        } else {
//            LOGGER.info("FM-upload | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator+ ApCo.PRA_FM_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("FM-upload | looking for file Name along with path:[{}]",fromFile);

        if(nseFileUtils.isFileAbsent(fromFile)) {
            LOGGER.warn("OM-upload | file not found: [{}]", fromFile);
            return;
        }

        Map<FmBean, FmBean> foBeanMap = csvReader.read(null, fromFile);
        //LOGGER.info("{}", foBeanMap.size());
        upload(foBeanMap);
    }


    private void upload(Map<FmBean, FmBean> foBeanMap) {
        NseOptionMarketTab optionTab = new NseOptionMarketTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordSkipped = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();

        foBeanMap.values().forEach( source -> {
            try {
                if("OPTIDX".equals(source.getInstrument()) || "OPTSTK".equals(source.getInstrument())) {
                    optionTab.reset();
                    optionTab.setInstrument(source.getInstrument());
                    optionTab.setSymbol(source.getSymbol());
                    optionTab.setExpiryDate(DateUtils.toLocalDate(source.getExpiry_Dt()));
                    optionTab.setStrikePrice(source.getStrike_Pr());
                    optionTab.setOptionType(source.getOption_Typ());
                    optionTab.setOpen(source.getOpen());
                    optionTab.setHigh(source.getHigh());
                    optionTab.setLow(source.getLow());
                    optionTab.setClose(source.getClose());
                    optionTab.setSettlePrice(source.getSettle_Pr());
                    optionTab.setContracts(source.getContracts());
                    optionTab.setValueInLakh(source.getVal_InLakh());
                    optionTab.setOpenInt(source.getOpen_Int());
                    optionTab.setChangeInOi(source.getChg_In_Oi());
                    optionTab.setTradeDate(DateUtils.toLocalDate(source.getTimestamp()));
                    optionMarketRepository.save(optionTab);
                    recordSucceed.incrementAndGet();
                } else {
                    recordSkipped.incrementAndGet();
                }
            }catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }

        });
        LOGGER.info("OM-upload | record - uploaded {}, skipped {}, failed: [{}]", recordSucceed.get(), recordSkipped.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("OM-upload | some record could not be persisted");
    }

}
