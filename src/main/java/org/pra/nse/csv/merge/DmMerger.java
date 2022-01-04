package org.pra.nse.csv.merge;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.csv.bean.in.DmBean;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.read.DmCsvReader;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class DmMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmMerger.class);

    private final PraFileUtils praFileUtils;
    private final DmCsvReader csvReader;

    public DmMerger(PraFileUtils praFileUtils, DmCsvReader csvReader) {
        this.praFileUtils = praFileUtils;
        this.csvReader = csvReader;
    }

    public void readAndMerge(List<PraBean> praBeans, LocalDate forDate) {
        LOGGER.info("MT-Merge | for date:[{}]", forDate);
        String fromFile;
        //fromFile = fileUtils.getLatestFileNameForMat(1);
        fromFile = praFileUtils.getLatestFileNameFor(PraCons.DM_FILES_PATH, PraCons.PRA_DM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT,1, forDate);
        Map<String, DmBean> mtLatestBeanMap = csvReader.read(fromFile);
        //fromFile = fileUtils.getLatestFileNameForMat(2);
        fromFile = praFileUtils.getLatestFileNameFor(PraCons.DM_FILES_PATH, PraCons.PRA_DM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT,2, forDate);
        Map<String, DmBean> matPreviousBeanMap = csvReader.read(fromFile);
        praBeans.forEach(praBean -> {
            if(mtLatestBeanMap.containsKey(praBean.getSymbol()) && matPreviousBeanMap.containsKey(praBean.getSymbol())) {
                praBean.setTdyDelivery(mtLatestBeanMap.get(praBean.getSymbol()).getDeliverableQty());
                praBean.setPrevsDelivery(matPreviousBeanMap.get(praBean.getSymbol()).getDeliverableQty());
                try {
                    if(mtLatestBeanMap.get(praBean.getSymbol()).getDeliverableQty() != 0
                            && matPreviousBeanMap.get(praBean.getSymbol()).getDeliverableQty() != 0) {
                        double pct = matPreviousBeanMap.get(praBean.getSymbol()).getDeliverableQty()/100d;
                        double diff = mtLatestBeanMap.get(praBean.getSymbol()).getDeliverableQty() - matPreviousBeanMap.get(praBean.getSymbol()).getDeliverableQty();
                        double pctChange = Math.round(diff / pct);
                        praBean.setPrcntChgInDelivery(pctChange);
                    }
                } catch (ArithmeticException ae) {
                    ae.printStackTrace();
                }
            } else {
                if(!praBean.getSymbol().contains("NIFTY")) {
                    LOGGER.warn("symbol not found in delivery map: {}", praBean.getSymbol());
                }
            }
        });
    }

}
