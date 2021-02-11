package org.pra.nse.csv.read;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.IdxBean;
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
public class IdxCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdxCsvReader.class);
    private final NseFileUtils nseFileUtils;


    IdxCsvReader(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public Map<String, IdxBean> read(String fromFile) {
        String toFile = PathHelper.fileNameWithFullPath(NseCons.IDX_DIR_NAME, ApCo.PRA_IDX_FILE_PREFIX, fromFile);
        if(nseFileUtils.isFileExist(toFile)) {
            LOGGER.info("IDX file exists: [{}]", toFile);
        } else {
            LOGGER.error("IDX file does not exist: [{}]", toFile);
        }

        Map<String, IdxBean> beanMap = readCsv(toFile);
        LOGGER.info("IDX Total Beans in Map: {}", beanMap.size());
        return beanMap;
    }

    private Map<String, IdxBean> readCsv(String fileName) {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            LOGGER.error("IDX csv file not found: {}", e);
        }
        final CellProcessor[] processors = getProcessors();

        IdxBean bean;
        String[] header;
        Map<String, IdxBean> beanMap = new LinkedHashMap<>();
        try {
            header = beanReader.getHeader(true);
            while( (bean = beanReader.read(IdxBean.class, header, processors)) != null ) {
//                LOGGER.info(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(), beanReader.getRowNumber()-1, bean));
                bean.setSymbol(bean.getIdxName());
                if(beanMap.containsKey(bean.getSymbol())) {
                    LOGGER.warn("Symbol already present in map: old value = [{}], new value = [{}]",
                            beanMap.get(bean.getSymbol()), bean);
                }
                beanMap.put(bean.getSymbol(), bean);
            }
        } catch (IOException e) {
            LOGGER.warn("some error: {}", e);
        }
        return beanMap;
    }

    private static CellProcessor[] getProcessors() {
        return new CellProcessor[] {
                new NotNull(), //idxName
                new ParseDate(ApCo.PRA_IDX_DATA_DATE_FORMAT), // tradeDate
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
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // pb
                new DMinMax(0L, DMinMax.MAX_DOUBLE) // divYield
        };
    }

}
