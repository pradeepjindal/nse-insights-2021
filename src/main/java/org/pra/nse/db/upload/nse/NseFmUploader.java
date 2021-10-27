package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.FmBean;
import org.pra.nse.csv.read.FmCsvReader;
import org.pra.nse.db.dao.FmDao;
import org.pra.nse.db.model.NseFutureMarketTab;
import org.pra.nse.db.repository.NseFmRepo;
import org.pra.nse.refdata.LotSizeService;
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
public class NseFmUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseFmUploader.class);

    private final NseFmRepo futureMarketRepository;
    private final FmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final FmCsvReader csvReader;

    private String fileDirName = ApCo.FM_DIR_NAME;
    private String filePrefix = ApCo.PRA_FM_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + ApCo.FM_DIR_NAME;

    public NseFmUploader(NseFmRepo repository,
                         FmDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         FmCsvReader fmCsvReader) {
        this.futureMarketRepository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = fmCsvReader;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_FM_FILE_AVAILABLE_FROM_DATE);
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
            LOGGER.info("FM-upload | already uploaded | for date:[{}]", forDate);
            return;
        } else {
//            LOGGER.info("FM-upload | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator+ ApCo.PRA_FM_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        //LOGGER.info("FM-upload | looking for file Name along with path:[{}]",fromFile);

        if(nseFileUtils.isFileAbsent(fromFile)) {
            LOGGER.warn("FM-upload | file not found: [{}]", fromFile);
            return;
        }

        Map<FmBean, FmBean> foBeanMap = csvReader.read(null, fromFile);
        //LOGGER.info("{}", foBeanMap.size());
        upload(forDate, foBeanMap);
    }


    private void upload(LocalDate forDate, Map<FmBean, FmBean> foBeanMap) {
        NseFutureMarketTab target = new NseFutureMarketTab();
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordSkipped = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();

        foBeanMap.values().forEach( source -> {
            try {
                if("FUTIVX".equals(source.getInstrument()) || "FUTIDX".equals(source.getInstrument()) || "FUTSTK".equals(source.getInstrument())) {
                    target.reset();
                    target.setInstrument(source.getInstrument());
                    target.setSymbol(source.getSymbol());
                    target.setExpiryDate(DateUtils.toLocalDate(source.getExpiry_Dt()));
                    target.setStrikePrice(source.getStrike_Pr());
                    target.setOptionType(source.getOption_Typ());
                    target.setOpen(source.getOpen());
                    target.setHigh(source.getHigh());
                    target.setLow(source.getLow());
                    target.setClose(source.getClose());
                    target.setSettlePrice(source.getSettle_Pr());
                    target.setContracts(source.getContracts());
                    target.setValueInLakh(source.getVal_InLakh());
                    target.setOpenInt(source.getOpen_Int());
                    target.setChangeInOi(source.getChg_In_Oi());
                    target.setTradeDate(DateUtils.toLocalDate(source.getTimestamp()));
                    //
                    target.setTds(forDate.toString());
                    target.setTdn(Integer.valueOf(forDate.toString().replace("-", "")));

                    LocalDate edt = DateUtils.toLocalDate(source.getExpiry_Dt());
                    target.setEds(edt.toString());
                    target.setEdn(Integer.valueOf(edt.toString().replace("-", "")));

                    LocalDate fix_expiry_date = LocalDate.of(edt.getYear(), edt.getMonthValue(), 25);
                    target.setFeds(fix_expiry_date.toString());
                    target.setFedn(Integer.valueOf(fix_expiry_date.toString().replace("-", "")));
                    //
                    target.setLotSize(LotSizeService.getLotSizeAsLong(source.getSymbol()));

                    futureMarketRepository.save(target);
                    recordSucceed.incrementAndGet();
                } else {
                    recordSkipped.incrementAndGet();
                }
            }catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }

        });
        LOGGER.info("FM-upload | record - uploaded {}, skipped {}, failed: [{}]", recordSucceed.get(), recordSkipped.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("FM-upload | some record could not be persisted");
    }
}
