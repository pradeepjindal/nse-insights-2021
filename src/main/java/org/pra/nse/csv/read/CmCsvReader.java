package org.pra.nse.csv.read;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.CmBean;
import org.pra.nse.exception.NseCmFileColumnMismatchRTE;
import org.pra.nse.refdata.IdxCategoryEnum;
import org.pra.nse.refdata.IdxFiveHundredService;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.Collections;
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
        //String toFile = PathHelper.fileNameWithFullPath(NseCons.CM_DIR_NAME, ApCo.PRA_CM_FILE_PREFIX, fromFile);
        if(nseFileUtils.isFilePresent(fromFile)) {
            //LOGGER.info("CM file exists: [{}]", fromFile);
        } else {
            LOGGER.error("CM file does not exist: [{}]", fromFile);
        }

        Map<String, CmBean> beanMap = Collections.emptyMap();
        try {
            beanMap = readCsv14(fromFile);
            LOGGER.info("Total CM Beans in Map: {}", beanMap.size());
        } catch(NseCmFileColumnMismatchRTE ncme) {
            beanMap = readCsv13(fromFile);
            LOGGER.info("Total CM Beans in Map: {}", beanMap.size());
        }

        return beanMap;
    }

    private Map<String, CmBean> readCsv13(String fileName) {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            LOGGER.error("cm csv file not found:", e);
        }
        final CellProcessor[] processors = getProcessors_for13Column();

        CmBean bean;
        String[] header;
        Map<String, CmBean> beanMap = new HashMap<>();
        boolean not_EQ_or_BE_series;
        try {
            header = beanReader.getHeader(true);
            while( (bean = beanReader.read(CmBean.class, header, processors)) != null ) {
                //LOGGER.info(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(), beanReader.getRowNumber(), bean));
//                if(IdxFiveHundredService.isNotMember(IdxCategoryEnum.IDX_500, bean.getSymbol()))
//                    continue;
                not_EQ_or_BE_series = ! ("EQ".equals(bean.getSeries()) || "BE".equals(bean.getSeries()));
                if(not_EQ_or_BE_series)
                    continue;
                if(beanMap.containsKey(bean.getSymbol())) {
                    LOGGER.warn("Symbol already present in map: old value = [{}], new value = [{}]",
                            beanMap.get(bean.getSymbol()), bean);
                }
                beanMap.put(bean.getSymbol(), bean);
            }
        } catch (SuperCsvException cse) {
            LOGGER.warn("some error:", cse);
            String errorMessage = "The number of columns to be processed (13) must match the number of CellProcessors (14): check that the number of CellProcessors you have defined matches the expected number of columns being read/written";
            if(cse.getMessage().equals(errorMessage))
                throw new NseCmFileColumnMismatchRTE();
        } catch (IOException e) {
            LOGGER.warn("some error:", e);
        }
        return beanMap;
    }

    private Map<String, CmBean> readCsv14(String fileName) {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            LOGGER.error("cm csv file not found:", e);
        }
        final CellProcessor[] processors = getProcessors_for14Column();

        CmBean bean;
        String[] header;
        Map<String, CmBean> beanMap = new HashMap<>();
        try {
            header = beanReader.getHeader(true);
            while( (bean = beanReader.read(CmBean.class, header, processors)) != null ) {
                //LOGGER.info(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(), beanReader.getRowNumber(), bean));
                if("EQ".equals(bean.getSeries())) {
                    if(beanMap.containsKey(bean.getSymbol())) {
                        LOGGER.warn("Symbol already present in map: old value = [{}], new value = [{}]",
                                beanMap.get(bean.getSymbol()), bean);
                    }
                    beanMap.put(bean.getSymbol(), bean);
                }
            }
        } catch (SuperCsvException cse) {
//            LOGGER.warn("some error: {}", cse);
            String errorMessage = "The number of columns to be processed (13) must match the number of CellProcessors (14): check that the number of CellProcessors you have defined matches the expected number of columns being read/written";
            if(cse.getMessage().equals(errorMessage))
                throw new NseCmFileColumnMismatchRTE();
        } catch (IOException e) {
            LOGGER.warn("some error:", e);
        }
        return beanMap;
    }

    private static CellProcessor[] getProcessors_for13Column() {
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
                new NotNull() // isin
                //, null
        };
    }
    private static CellProcessor[] getProcessors_for14Column() {
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
                new NotNull() // isin
                , null
        };
    }

}
