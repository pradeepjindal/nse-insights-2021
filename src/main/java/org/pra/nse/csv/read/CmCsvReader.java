package org.pra.nse.csv.read;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.CmBean;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class CmCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmCsvReader.class);
    private final NseFileUtils nseFileUtils;


    CmCsvReader(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public Map<String, CmBean> read(String fromFile) {
        String toFile = PathHelper.fileNameWithFullPath(NseCons.CM_DIR_NAME, ApCo.PRA_CM_FILE_PREFIX, fromFile);
        if(nseFileUtils.isFileExist(toFile)) {
            LOGGER.info("CM file exists: [{}]", toFile);
        } else {
            LOGGER.error("CM file does not exist: [{}]", toFile);
        }

        Map<String, CmBean> beanMap = readCsv(toFile);
        LOGGER.info("Total CM Beans in Map: {}", beanMap.size());
        return beanMap;
    }

    private Map<String, CmBean> readCsv(String fileName) {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            LOGGER.error("cm csv file not found: {}", e);
        }
        final CellProcessor[] processors = getProcessors();

        CmBean cmBean;
        String[] header;
        Map<String, CmBean> cmBeanMap = new HashMap<>();
        try {
            header = beanReader.getHeader(true);
            while( (cmBean = beanReader.read(CmBean.class, header, processors)) != null ) {
                //LOGGER.info(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(), beanReader.getRowNumber(), matBean));
                if("EQ".equals(cmBean.getSeries())) {
                    if(cmBeanMap.containsKey(cmBean.getSymbol())) {
                        LOGGER.warn("Symbol already present in map: old value = [{}], new value = [{}]",
                                cmBeanMap.get(cmBean.getSymbol()), cmBean);
                    }
                    cmBeanMap.put(cmBean.getSymbol(), cmBean);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("some error: {}", e);
        }
        return cmBeanMap;
    }

    private static CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                new NotNull(), //symbol
                new NotNull(), //series
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // open
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // high
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // low
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // close
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // last
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // prevClose

                new LMinMax(0L, LMinMax.MAX_LONG), // tot trd qty
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // tot trd val
                new ParseDate(ApCo.PRA_CM_DATA_DATE_FORMAT), // timestamp
                new LMinMax(0L, LMinMax.MAX_LONG), // totalTrades
                new NotNull(), // isin
                null
        };
    }

}
