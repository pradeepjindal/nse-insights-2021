package org.pra.nse.csv.merge;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.CmBean;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.read.CmCsvReader;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class CmMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmMerger.class);

    private final PraFileUtils praFileUtils;
    private final CmCsvReader csvReader;

    public CmMerger(PraFileUtils praFileUtils, CmCsvReader cmCsvReader) {
        this.praFileUtils = praFileUtils;
        this.csvReader = cmCsvReader;
    }

    public void readAndMerge(List<PraBean> praBeans, LocalDate forDate) {
        LOGGER.info("CM-Merge | for date:[{}]", forDate);
        String fromFile;
        //fromFile = fileUtils.getLatestFileNameForCm(1);
        fromFile = praFileUtils.getLatestFileNameFor(PraCons.CM_FILES_PATH, ApCo.PRA_CM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT,1, forDate);
        Map<String, CmBean> latestBeanMap = csvReader.read(fromFile);
        fromFile = null;
        //fromFile = fileUtils.getLatestFileNameForCm(2);
        fromFile = praFileUtils.getLatestFileNameFor(PraCons.CM_FILES_PATH, ApCo.PRA_CM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT,2, forDate);
        Map<String, CmBean> previousBeanMap = csvReader.read(fromFile);
        praBeans.forEach(praBean -> {
            String symbol = praBean.getSymbol();
            if(latestBeanMap.containsKey(symbol) && previousBeanMap.containsKey(symbol)) {
                praBean.setCmOpen(latestBeanMap.get(symbol).getOpen());
                praBean.setCmHigh(latestBeanMap.get(symbol).getHigh());
                praBean.setCmLow(latestBeanMap.get(symbol).getLow());
                praBean.setCmClose(latestBeanMap.get(symbol).getClose());

                praBean.setCmTdyClose(latestBeanMap.get(symbol).getClose());
                praBean.setCmPrevsClose(previousBeanMap.get(symbol).getClose());
                praBean.setCmTdyTraded(latestBeanMap.get(symbol).getTotTrdQty());
                try{
                    if(latestBeanMap.get(symbol).getClose() != 0 && previousBeanMap.get(symbol).getClose() != 0) {
                        double pct = previousBeanMap.get(symbol).getClose()/100d;
                        double diff = latestBeanMap.get(symbol).getClose() - previousBeanMap.get(symbol).getClose();
                        double pctChange = Math.round(diff / pct);
                        praBean.setCmPrcntChgInClose(pctChange);
                    }
                } catch (ArithmeticException ae) {
                    ae.printStackTrace();
                }
                if("FUTSTK".equals(praBean.getInstrument()))
                    calculateHammer(praBean, latestBeanMap.get(symbol), previousBeanMap.get(symbol));
            } else {
                if(!praBean.getSymbol().contains("NIFTY")) {
                    LOGGER.warn("symbol not found in delivery map: {}", praBean.getSymbol());
                }
            }
        });
    }

    private void calculateHammer(PraBean praBean, CmBean latestCmBean, CmBean previousCmBean) {
        double highLow = latestCmBean.getHigh() - latestCmBean.getLow();
        double openClose = latestCmBean.getOpen() - latestCmBean.getClose();
        double closeLow = latestCmBean.getClose() - latestCmBean.getLow();
        double openLow = latestCmBean.getOpen() - latestCmBean.getLow();
        //double previousOpenClose = previousCmBean.getOpen() + previousCmBean.getClose();
        if( //previousOpenClose / 2 > latestCmBean.getOpen() &&
                highLow > (3 * openClose)
                && closeLow / (0.001 + highLow) > 0.6
                && openLow / (0.001 + highLow) > 0.6
        ) {
            //LOGGER.info("Hammer Detected: {}", cmBean.getSymbol());
            praBean.setHammer("Hammer");
        } else {
            //LOGGER.info("Hammer         : {}", cmBean.getSymbol());
        }
    }

}
