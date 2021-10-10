package org.pra.nse.csv.merge;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.FmBean;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.read.FmCsvReader;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class FmMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmMerger.class);

    private final PraFileUtils praFileUtils;
    private final FmCsvReader csvReader;

    public FmMerger(PraFileUtils praFileUtils, FmCsvReader csvReader) {
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }

    public TreeSet<LocalDate> readAndMerge(List<PraBean> praBeans, LocalDate forDate) throws FileNotFoundException {
        LOGGER.info("FO-Merge | for date:[{}]", forDate);
        Map<FmBean, FmBean> foBeanMap;
        //String foLatestFileName = fileUtils.getLatestFileNameForFo(1);
        String foLatestFileName = praFileUtils.getLatestFileNameFor(PraCons.FM_FILES_PATH, ApCo.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT,1, forDate);
        if(null==foLatestFileName) throw new RuntimeException("Current FM File does not exist - aborting further processing");
        LOGGER.info("latestFileName FO: " + foLatestFileName);
        //String foPreviousFileName = fileUtils.getLatestFileNameForFo(2);
        String foPreviousFileName = praFileUtils.getLatestFileNameFor(PraCons.FM_FILES_PATH, ApCo.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT,2, forDate);
        if(null==foPreviousFileName) throw new RuntimeException("Previous FM File does not exist - aborting further processing");
        LOGGER.info("previousFileName FO: " + foPreviousFileName);

        // FO
        foBeanMap = csvReader.read(null, foLatestFileName);
        //LOGGER.info("{}", foBeanMap.size());

        csvReader.read(foBeanMap, foPreviousFileName);
        //LOGGER.info("{}", foBeanMap.size());

        return merge(praBeans, foBeanMap);
        //praBeans.forEach(bean -> LOGGER.info(bean));
        //LOGGER.info("{}", praBeans.size());
    }

    public TreeSet<LocalDate> merge(List<PraBean> praBeans, Map<FmBean, FmBean> foBeanMap) {
        TreeSet<LocalDate> foMonthlyExpiryDates = new TreeSet<>();
        foBeanMap.entrySet().forEach( entry -> {

            FmBean todayBean = entry.getKey();
            FmBean previousBean = entry.getValue();
            PraBean praBean = new PraBean();
            //
            if("FUTSTK".equals(todayBean.getInstrument())) {
                foMonthlyExpiryDates.add(DateUtils.toLocalDate(todayBean.getExpiry_Dt()));
            }

            praBean.setInstrument(todayBean.getInstrument());
            praBean.setSymbol(todayBean.getSymbol());
            praBean.setExpiryLocalDate(DateUtils.toLocalDate(todayBean.getExpiry_Dt()));
            praBean.setExpiryDate(todayBean.getExpiry_Dt());
            praBean.setStrikePrice(todayBean.getStrike_Pr());
            praBean.setOptionType(todayBean.getOption_Typ());
            //
            praBean.setContracts(todayBean.getContracts());
            //
            praBean.setFoTdyClose(todayBean.getClose());
            praBean.setTdyOI(todayBean.getOpen_Int());
            //
            praBean.setTdyLocalDate(DateUtils.toLocalDate(todayBean.getTimestamp()));
            praBean.setTdyDate(todayBean.getTimestamp());
            //
            praBean.setFoPrevsClose(previousBean.getClose());
            praBean.setPrevsOI(previousBean.getOpen_Int());
            praBean.setPrevsLocalDate(previousBean.getTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            praBean.setPrevsDate(previousBean.getTimestamp());
            //
            // calc fields
            try{
                if(todayBean.getClose() != 0 && previousBean.getClose() != 0) {
                    double pct = previousBean.getClose()/100d;
                    double diff = todayBean.getClose() - previousBean.getClose();
                    double pctChange = Math.round(diff / pct);
                    praBean.setFoPrcntChgInClose(pctChange);
                }
            } catch (ArithmeticException ae) {
                ae.printStackTrace();
            }
            try{
                if(todayBean.getChg_In_Oi() != 0 && previousBean.getOpen_Int() != 0) {
                    double pct = previousBean.getOpen_Int()/100d;
                    double diff = todayBean.getOpen_Int() - previousBean.getOpen_Int();
                    double pctChange = Math.round(diff / pct);
                    praBean.setPrcntChgInOI(pctChange);
                }
            } catch (ArithmeticException ae) {
                ae.printStackTrace();
            }
            praBeans.add(praBean);
        });
        LOGGER.info("praBeans: {}", praBeans.size());
        return foMonthlyExpiryDates;
    }

}
