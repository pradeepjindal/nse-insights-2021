package org.pra.nse.csv.read;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.csv.bean.in.DmBean;
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
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;

@Component
public class DmCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmCsvReader.class);

    private final NseFileUtils nseFileUtils;

    DmCsvReader(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public Map<String, DmBean> read(String fromFile) {
        //String toFile = PathHelper.fileNameWithFullPath(NseCons.DM_DIR_NAME, ApCo.PRA_DM_FILE_PREFIX, fromFile);
        if(nseFileUtils.isFilePresent(fromFile)) {
            //LOGGER.info("Mat file exist [{}]", fromFile);
        } else {
            LOGGER.error("Mat file does not exist [{}]", fromFile);
        }
        //
        Map<String, DmBean> beanMap = readCsv(fromFile);
        LOGGER.info("Total MT Beans in Map: {}", beanMap.size());
        return beanMap;
    }

    private Map<String, DmBean> readCsv(String fileName) {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            LOGGER.error("DM csv file not found:");
        }
        final CellProcessor[] processors = getProcessors();

        DmBean bean;
        String[] header;
        Map<String, DmBean> beanMap = new HashMap<>();
        boolean not_EQ_or_BE_series;
        try {
            header = beanReader.getHeader(true);
            if (header == null) {
                LOGGER.warn("Mat file has ZERO size");
                return beanMap;
            }
            while( (bean = beanReader.read(DmBean.class, header, processors)) != null ) {
                //LOGGER.info(String.format("lineNo=%s, rowNo=%s, customer=%s", beanReader.getLineNumber(), beanReader.getRowNumber(), matBean));
//                if(IdxFiveHundredService.isNotMember(IdxCategoryEnum.IDX_500, bean.getSymbol()))
//                    continue;
                not_EQ_or_BE_series = ! ("EQ".equals(bean.getSecurityType()) || "BE".equals(bean.getSecurityType()));
                if(not_EQ_or_BE_series)
                    continue;
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
                new LMinMax(0L, LMinMax.MAX_LONG), // recType
                new LMinMax(0L, LMinMax.MAX_LONG), // srNo
                new NotNull(), // symbol
                new NotNull(), // securityType
                new LMinMax(0L, LMinMax.MAX_LONG), // tradedQty
                new LMinMax(0L, LMinMax.MAX_LONG), // deliverableQty
                new DMinMax(0L, DMinMax.MAX_DOUBLE), // deliveryToTradeRatio
                new ParseDate(ApCo.PRA_DM_DATA_DATE_FORMAT) // tradeDate
        };
    }

}
