package org.pra.nse.db.upload.nse;

import org.pra.nse.ApCo;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.FmBean;
import org.pra.nse.csv.read.OmCsvReader;
import org.pra.nse.db.dao.NseOmDao;
import org.pra.nse.db.model.NseOptionMarketTab;
import org.pra.nse.db.repository.NseFoRepo;
import org.pra.nse.db.repository.NseOmRepo;
import org.pra.nse.refdata.LotSizeService;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.LotSizeUtil;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NseOmUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseOmUploader.class);

    private final NseOmRepo optionMarketRepository;
    private final NseOmDao dao;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final OmCsvReader csvReader;
    private final NseFoRepo nseFoRepo;

    private String fileDirName = PraCons.FM_DIR_NAME;
    private String filePrefix = PraCons.PRA_FM_FILE_PREFIX;
    private LocalDate defaultDate = ApCo.UPLOAD_NSE_FROM_DATE;

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + PraCons.FM_DIR_NAME;

    public NseOmUploader(NseOmRepo repository,
                         NseOmDao dao,
                         NseFileUtils nseFileUtils,
                         PraFileUtils praFileUtils,
                         OmCsvReader omCsvReader,
                         NseFoRepo nseFoRepo) {
        this.optionMarketRepository = repository;
        this.dao = dao;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.csvReader = omCsvReader;
        this.nseFoRepo = nseFoRepo;
    }

    public void uploadAll() {
        uploadFromDate(ApCo.NSE_FM_FILE_AVAILABLE_FROM_DATE);
    }
    public void upload2021() {
        uploadFromDate(LocalDate.of(2021, 9, 1));
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
            //LOGGER.info("om | upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
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
        if(DateUtils.notTradingDay(forDate)) return;

        if(dao.dataCount(forDate) > 0) {
            LOGGER.info("om | already uploaded - for date:[{}]", forDate);
            return;
        } else {
            LOGGER.info("om | uploading - for date:[{}]", forDate);
        }

        String fromFile = Data_Dir + File.separator + PraCons.PRA_FM_FILE_PREFIX + forDate + ApCo.DATA_FILE_EXT;
        //LOGGER.info("FM-upload | looking for file Name along with path:[{}]",fromFile);

        if(nseFileUtils.isFileAbsent(fromFile)) {
            LOGGER.warn("om | file not found: [{}]", fromFile);
            return;
        }

        Map<FmBean, FmBean> foBeanMap = csvReader.read(null, fromFile);
        //LOGGER.info("{}", foBeanMap.size());
//        upload(forDate, foBeanMap);
        uploadBulk(forDate, foBeanMap);
    }

    private void uploadBulk(LocalDate forDate, Map<FmBean, FmBean> foBeanMap) {
        Map<String, Map<LocalDate, Integer>> symbol_ed_ls_map = LotSizeUtil.transform(nseFoRepo, forDate);

        //
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordSkipped = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();

        List<NseOptionMarketTab> list = new ArrayList<>();
        foBeanMap.values().forEach( source -> {
            try {
                if("OPTIDX".equals(source.getInstrument()) || "OPTSTK".equals(source.getInstrument())) {
                    NseOptionMarketTab target = new NseOptionMarketTab();
                    target.setInstrument(source.getInstrument());
                    target.setSymbol(source.getSymbol());
                    target.setExpiryDate(source.getExpiry_Dt());
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

                    LocalDate edt = source.getExpiry_Dt();
                    target.setEds(edt.toString());
                    target.setEdn(Integer.valueOf(edt.toString().replace("-", "")));

                    LocalDate fix_expiry_date = LocalDate.of(edt.getYear(), edt.getMonthValue(), 25);
                    target.setFeds(fix_expiry_date.toString());
                    target.setFedn(Integer.valueOf(fix_expiry_date.toString().replace("-", "")));

                    if(symbol_ed_ls_map.containsKey(source.getSymbol()) && symbol_ed_ls_map.get(source.getSymbol()).containsKey(edt)) {
                        Integer foLotSize = symbol_ed_ls_map.get(source.getSymbol()).get(edt);
                        target.setLotSize(foLotSize);
                    } else {
                        //LOGGER.warn("fo lotSize not found, symbol: {}, td: {}, ed: {}", source.getSymbol(), forDate, edt);
                    }

                    list.add(target);
                    recordSucceed.incrementAndGet();
                } else {
                    recordSkipped.incrementAndGet();
                }
            }catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });

        int batchSize = 1000;
        List<NseOptionMarketTab> batchList;
        LOGGER.info("om | total records to be saved = {}", list.size());
        for(int i = 0; i < list.size(); i = i + batchSize) {
            if(i + batchSize < list.size()) {
//                LOGGER.info("from: {}, to: {}", i, i + batchSize -1);
                batchList = list.subList(i, i + batchSize);
            } else {
//                LOGGER.info("from: {}, to: {}", i, list.size() -1);
                batchList = list.subList(i, list.size());
            }
            optionMarketRepository.saveAll(batchList);
        }

        LOGGER.info("om | record - uploaded {}, skipped {}, failed: [{}]", recordSucceed.get(), recordSkipped.get(), recordFailed.get());
        if (recordFailed.get() > 0) throw new RuntimeException("om | some record could not be persisted");
    }

}
