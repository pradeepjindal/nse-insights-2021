package org.pra.nse.csv.writer;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.out.ManishBean;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.db.dao.NseViewDao;
import org.pra.nse.db.dto.PivotOiDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ManishCsvWriterB {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManishCsvWriterB.class);

    private final NseFileUtils nseFileUtils;
    private final NseViewDao nseViewDao;

    public ManishCsvWriterB(NseFileUtils nseFileUtils, NseViewDao nseViewDao) {
        this.nseFileUtils = nseFileUtils;
        this.nseViewDao = nseViewDao;
    }

    public void write(List<PraBean> praBeans, String outputFilePathAndName, TreeSet<LocalDate> foExpiryDates) throws IOException {
        //List<ManishBean> manishBeans = convert(praBeans, foExpiryDates);
        Map<String, ManishBean> manishBeanMap = convert(praBeans, foExpiryDates);
        List<ManishBean> manishBeans = enrich(manishBeanMap);

        //nseFileUtils.createFolder(outputFilePathAndName);
        //DirUtils.ensureDirPath(outputFilePathAndName);
        DirUtils.ensureFolder(ApCo.MANISH_DIR_NAME);

        //final ICsvBeanWriter beanWriter;
        try (ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(outputFilePathAndName), CsvPreference.STANDARD_PREFERENCE)) {
            // the header elements are used to map the bean values to each column (names must match)
            final String[] header = new String[] {
                    "symbol", "expiryDate", "tdyDate"
                    , "cmOpen", "cmHigh", "cmLow", "cmClose", "tdyTraded"
                    , "tdyDelivery","deliveryToTradeRatio"
                    , "fuClose"
                    , "oiOne", "oiTwo", "oiThree"
            };
            final CellProcessor[] processors = getProcessors();
            // write the header
            beanWriter.writeHeader(header);
            manishBeans.forEach( manishBean -> {
                try {
                    beanWriter.write(manishBean, header, processors);
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
                new NotNull(),
                new NotNull(),
                new NotNull(),
                // tdyTraded
                new LMinMax(0L, LMinMax.MAX_LONG),
                // todayDelivery
                new LMinMax(0L, LMinMax.MAX_LONG),
                // deliveryToTradeRatio
                new NotNull(),
                // todayOpenInterest
                new NotNull(),

                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG),
                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG),
                new LMinMax(LMinMax.MIN_LONG, LMinMax.MAX_LONG)
        };
    }

    private Map<String, ManishBean> convert(List<PraBean> praBeans, TreeSet<LocalDate> foExpiryDates) {
        Map<String, ManishBean> manishBeanMap = new HashMap<>();
        List<LocalDate> foExpiryDateAscList = new ArrayList<>();
        praBeans.forEach( praBean -> {
            if("FUTSTK".equals(praBean.getInstrument()) && praBean.getExpiryLocalDate().equals(foExpiryDates.first())) {
                ManishBean manishBean = new ManishBean();
                manishBean.setSymbol(praBean.getSymbol());
                manishBean.setExpiryDate(praBean.getExpiryDate());
                manishBean.setTdyDate(praBean.getTdyDate());

                manishBean.setCmOpen(praBean.getCmOpen());
                manishBean.setCmHigh(praBean.getCmHigh());
                manishBean.setCmLow(praBean.getCmLow());
                manishBean.setCmClose(praBean.getCmClose());

                manishBean.setFuClose(praBean.getFoTdyClose());

                manishBean.setCmClose(praBean.getCmTdyClose());
                manishBean.setTdyTraded(praBean.getCmTdyTraded());
                manishBean.setTdyDelivery(praBean.getTdyDelivery());
                double onePct = praBean.getCmTdyTraded() == 0 ? 1 : praBean.getCmTdyTraded()/100;
                double deliveryToTradeRatio = praBean.getTdyDelivery() / onePct;
                manishBean.setDeliveryToTradeRatio(deliveryToTradeRatio);
                manishBean.setTdyOI(praBean.getTdyOI());
                manishBeanMap.put(manishBean.getSymbol(),manishBean);
            }
        });

        praBeans.forEach( praBean -> {
            if("FUTSTK".equals(praBean.getInstrument())) {

                ManishBean manishBean = manishBeanMap.get(praBean.getSymbol());

                if(praBean.getExpiryLocalDate().equals(foExpiryDateAscList.get(0))) {
                    manishBean.setOiOne(praBean.getTdyOI());
                } else if(praBean.getExpiryLocalDate().equals(foExpiryDateAscList.get(1))) {
                    manishBean.setOiTwo(praBean.getTdyOI());
                } else if(praBean.getExpiryLocalDate().equals(foExpiryDateAscList.get(2))) {
                    manishBean.setOiThree(praBean.getTdyOI());
                } else {
                    LOGGER.warn("unexpected fourth expiry date found");
                }
            }
        });

        return manishBeanMap;
    }

    private List<ManishBean> enrich(Map<String, ManishBean> manishBeanMap) {
        return manishBeanMap.values().stream().collect(Collectors.toList());
    }

}
