package org.pra.nse.refdata;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FmStocks {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmStocks.class);
    private static final String fileName = "fm-lots.csv";
    private static List<LotSizeBean> lotSizeBeans;
    private static Map<String, Long> lotSizeMap;

    public static long getLotSizeValue(String symbol) {
        if(lotSizeBeans == null) {
            try {
                //String filePath = ApCo.ROOT_DIR +File.separator+ "ref-data" +File.separator+ fileName;
                //File csvFile = new File(filePath);
                File refFile = readFile("data/" + fileName);
                readCsv2(refFile);
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading " + fileName, e);
            }
        }
        return lotSizeMap.getOrDefault(symbol, 0L);
    }

    public static BigDecimal getLotSizeObject(String symbol) {
        if(lotSizeBeans == null) {
            try {
                //String filePath = ApCo.ROOT_DIR +File.separator+ "ref-data" +File.separator+ fileName;
                //File csvFile = new File(filePath);
                File refFile = readFile("data/" + fileName);
                readCsv2(refFile);
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading " + fileName, e);
            }
        }
        return new BigDecimal(lotSizeMap.getOrDefault(symbol, 0L));
    }

    private static File readFile(String fileName) {
        return new File(FmStocks.class.getClassLoader().getResource(fileName).getFile());
    }

    private static void readCsv2(File refFile) {
        lotSizeBeans = new ArrayList<>();
        lotSizeMap = new HashMap<>();
        try {
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            //TODO put this file into resource folder
            MappingIterator<LotSizeBean> it = mapper.readerFor(LotSizeBean.class).with(schema).readValues(refFile);
            //return it.readAll();
            LotSizeBean bean = null;
            while (it.hasNextValue()) {
                bean = it.nextValue();
                //LOGGER.info("{}", bean);
                lotSizeBeans.add(bean);
                lotSizeMap.put(bean.getSymbol(), bean.getSize());
            }
            LOGGER.info("LotSize, Total Rows Count: [{}]", lotSizeBeans.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred while transforming " + fileName, e);
            //return Collections.emptyList();
        }
        //lotSizeBeans.forEach( row -> LOGGER.info("{}", row));
    }

    private static void readCsv(String filePath) {
        List<LotSizeBean> beans = null;
        try {
            //csvFile = ResourceUtils.getFile("classpath:screens.csv");
            File csvFile = new File(filePath);
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = mapper.schemaFor(LotSizeBean.class).withHeader();
            MappingIterator<LotSizeBean> it = mapper.readerFor(LotSizeBean.class).with(schema).readValues(csvFile);
            //return it.readAll();
            LotSizeBean bean = null;
            while (it.hasNextValue()) {
                bean = it.nextValue();
                LOGGER.info("{}", bean);
                lotSizeBeans.add(bean);
                lotSizeMap.put(bean.getSymbol(), bean.getSize());
            }
            LOGGER.info("CSV, Total Rows Count: [{}]", beans.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading object list from file " + filePath, e);
            //return Collections.emptyList();
        }
        beans.forEach( row -> LOGGER.info("{}", row));
    }

}
