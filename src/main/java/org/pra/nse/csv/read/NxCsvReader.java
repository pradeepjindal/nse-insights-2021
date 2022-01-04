package org.pra.nse.csv.read;

import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.NxBean;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class NxCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(NxCsvReader.class);
    private final NseFileUtils nseFileUtils;

    NxCsvReader(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public Map<String, NxBean> read(String fromFile) {
        if(nseFileUtils.isFilePresent(fromFile)) {
//            LOGGER.info("IDX file exists: [{}]", fromFile);
        } else {
            LOGGER.error("IDX file does not exist: [{}]", fromFile);
        }

        Map<String, NxBean> beanMap = readCsv(fromFile);
        LOGGER.info("IDX Total Beans in Map: {}", beanMap.size());
        return beanMap;
    }

    private Map<String, NxBean> readCsv(String fileName) {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            LOGGER.error("IDX csv file not found:");
        }
        final CellProcessor[] processors = getProcessors();

        NxBean bean;
        String[] header;
        Map<String, NxBean> beanMap = new LinkedHashMap<>();
        try {
            header = beanReader.getHeader(true);
            if (header == null) {
                LOGGER.warn("IDX file has ZERO size");
                return beanMap;
            }
            while( (bean = beanReader.read(NxBean.class, header, processors)) != null ) {
                //LOGGER.info(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(), beanReader.getRowNumber()-1, bean));
                bean.setSymbol(bean.getIdxName());
                if(beanMap.containsKey(bean.getSymbol())) {
                    LOGGER.warn("Symbol already present in map: old value = [{}], new value = [{}]",
                            beanMap.get(bean.getSymbol()), bean);
                }
                beanMap.put(bean.getSymbol(), bean);
            }
        } catch (IOException e) {
            LOGGER.warn("some error:", e);
        }
        return beanMap;
    }

    private static CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                new NotNull(), //idxName
                new ParseDate(ApCo.PRA_NX_DATA_DATE_FORMAT), // tradeDate
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // open
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // high
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // low
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // close

                new DMinMax(-9999L, DMinMax.MAX_DOUBLE), // PointsChgAbs
                new DMinMax(-9999L, DMinMax.MAX_DOUBLE), // PointsChgPct
                new LMinMax(0L, LMinMax.MAX_LONG), // volume
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // turnOverInCrore

                //new DMinMax(0L, DMinMax.MAX_DOUBLE), // pe
                new Optional(new DMinMax(0L, DMinMax.MAX_DOUBLE)),
                //new DMinMax(0L, DMinMax.MAX_DOUBLE), // pb
                new Optional(new DMinMax(0L, DMinMax.MAX_DOUBLE)),
                //new DMinMax(0L, DMinMax.MAX_DOUBLE) // divYield
                new Optional(new DMinMax(0L, DMinMax.MAX_DOUBLE))
        };
    }

}
