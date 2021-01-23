package org.pra.nse.csv.writer;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.util.DirUtils;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeSet;

@Component
public class PradeepCsvWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PradeepCsvWriter.class);

    private final NseFileUtils nseFileUtils;

    public PradeepCsvWriter(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public void write(List<PraBean> praBeans, String outputFilePathAndName, TreeSet<LocalDate> foExpiryDates) throws IOException {
        //nseFileUtils.createFolder(outputFilePathAndName);
        //DirUtils.ensureDirPath(outputFilePathAndName);
        DirUtils.ensureFolder(ApCo.PRADEEP_DIR_NAME);
        //final ICsvBeanWriter beanWriter;
        try (ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(outputFilePathAndName), CsvPreference.STANDARD_PREFERENCE)) {
            // the header elements are used to map the bean values to each column (names must match)
            final String[] header = new String[] { "instrument", "symbol", "expiryDate", "strikePrice", "optionType"
                    , "contracts", "hammer"
                    , "cmTdyClose", "cmPrcntChgInClose"
                    , "foTdyClose", "foPrcntChgInClose"
                    , "tdyOI", "prcntChgInOI"
                    , "tdyDelivery","prcntChgInDelivery"
                    , "tdyDate"
                    , "cmPrevsClose", "foPrevsClose", "prevsOI", "prevsDelivery"
                    , "prevsDate"
            };
            final CellProcessor[] processors = getProcessors();
            // write the header
            beanWriter.writeHeader(header);
            // write the beans
//            for( final PraBean praBean : praBeans ) {
//                beanWriter.write(praBean, header, processors);
//            }
            praBeans.forEach( praBean -> {
                // pra TODO
                if("FUTSTK".equals(praBean.getInstrument()) && praBean.getExpiryLocalDate().equals(foExpiryDates.first())) {
                    try {
                        beanWriter.write(praBean, header, processors);
                    } catch (IOException e) {
                        LOGGER.warn("some error: {}", e.getMessage());
                    }
                }
            });
        }
    }


    private static CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                new NotNull(), // instrument
                new NotNull(), // symbol
                new FmtDate(ApCo.REPORTS_DATE_FORMAT), // expiryDate
                //new DMinMax(DMinMax.MIN_DOUBLE, DMinMax.MAX_DOUBLE), // strikePrice
                new NotNull(),
                new NotNull(), // optionType

                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG), // Contracts
                null,

                new NotNull(), // cmTodayClose
                new NotNull(),

                new DMinMax(DMinMax.MIN_DOUBLE, DMinMax.MAX_DOUBLE), // TodayClose
                //new DMinMax(DMinMax.MIN_DOUBLE, DMinMax.MAX_DOUBLE), // percentChangeInOpenInterest
                new NotNull(),
                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG), // TodayOpenInterest
                //new DMinMax(DMinMax.MIN_DOUBLE, DMinMax.MAX_DOUBLE), // percentChangeInClose
                new NotNull(),

                new LMinMax(0L, LMinMax.MAX_LONG), // todayDelivery
                new NotNull(), // prcntChgInDelivery

                new FmtDate(ApCo.REPORTS_DATE_FORMAT), // TodayTradeDate

                new NotNull(),
                new DMinMax(0D, DMinMax.MAX_DOUBLE), // PreviousClose
                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG), // PreviousOpenInterest
                new LMinMax(0L, LMinMax.MAX_LONG), // previousDelivery
                new FmtDate(ApCo.REPORTS_DATE_FORMAT) // PreviousTradeDate
        };
    }

}
