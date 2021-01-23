package org.pra.nse.csv.writer;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.out.ManishBean;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.util.DirUtils;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Component
public class ManishCsvWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManishCsvWriter.class);

    private final NseFileUtils nseFileUtils;

    public ManishCsvWriter(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public void write(List<PraBean> praBeans, String outputFilePathAndName, TreeSet<LocalDate> foExpiryDates) throws IOException {
        List<ManishBean> manishBeans = convert(praBeans, foExpiryDates);
        //nseFileUtils.createFolder(outputFilePathAndName);
        //DirUtils.ensureDirPath(outputFilePathAndName);
        DirUtils.ensureFolder(ApCo.MANISH_DIR_NAME);

        //final ICsvBeanWriter beanWriter;
        try (ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(outputFilePathAndName), CsvPreference.STANDARD_PREFERENCE)) {
            // the header elements are used to map the bean values to each column (names must match)
            final String[] header = new String[] {
                    "symbol", "expiryDate", "tdyDate"
                    , "tdyClose", "tdyTraded"
                    , "tdyDelivery","deliveryToTradeRatio"
                    , "tdyOI"
            };
            final CellProcessor[] processors = getProcessors();
            // write the header
            beanWriter.writeHeader(header);
            manishBeans.forEach( praBean -> {
                try {
                    beanWriter.write(praBean, header, processors);
                } catch (IOException e) {
                    LOGGER.warn("some error: {}", e.getMessage());
                }
            });
        }
    }

    private static CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                // symbol
                new NotNull(),
                // expiryDate
                new FmtDate(ApCo.REPORTS_DATE_FORMAT),
                // Date
                new FmtDate(ApCo.REPORTS_DATE_FORMAT),
                // cmTodayClose
                new NotNull(),
                // tdyTraded
                new LMinMax(0L, LMinMax.MAX_LONG),
                // todayDelivery
                new LMinMax(0L, LMinMax.MAX_LONG),
                // deliveryToTradeRatio
                new NotNull(),
                // todayOpenInterest
                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG)
        };
    }

    private List<ManishBean> convert(List<PraBean> praBeans, TreeSet<LocalDate> foExpiryDates) {
        List<ManishBean> manishBeans = new ArrayList<>();
        praBeans.forEach( praBean -> {
            if("FUTSTK".equals(praBean.getInstrument()) && praBean.getExpiryLocalDate().equals(foExpiryDates.first())) {
                ManishBean manishBean = new ManishBean();
                manishBean.setSymbol(praBean.getSymbol());
                manishBean.setExpiryDate(praBean.getExpiryDate());
                manishBean.setTdyDate(praBean.getTdyDate());
                manishBean.setTdyClose(praBean.getCmTdyClose());
                manishBean.setCmClose(praBean.getCmTdyClose());
                manishBean.setTdyTraded(praBean.getCmTdyTraded());
                manishBean.setTdyDelivery(praBean.getTdyDelivery());
                double deliveryToTradeRatio = praBean.getTdyDelivery() / (praBean.getCmTdyTraded()/100);
                manishBean.setDeliveryToTradeRatio(deliveryToTradeRatio);
                manishBean.setTdyOI(praBean.getTdyOI());
                manishBeans.add(manishBean);
            }
        });
        return manishBeans;
    }
}
