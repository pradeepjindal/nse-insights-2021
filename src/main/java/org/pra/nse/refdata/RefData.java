package org.pra.nse.refdata;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.ApCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefData {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefData.class);

    private static List<LotSizeBean> lotSizeBeans;
    private static Map<String, Long> lotSizeMap;

    public static long getLotSize(String symbol) {
        if(lotSizeBeans == null) {
            try {
                //String filePath = ApCo.ROOT_DIR +File.separator+ "ref-data" +File.separator+ "fm-lots.csv";
                //File csvFile = new File(filePath);
                File refFile = readFile("data/fm-lots.csv");
                readCsvNew(refFile);
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading fm-lots.csv", e);
            }
        }
        return lotSizeMap.getOrDefault(symbol, 0L);
    }

    private static File readFile(String fileName) {
        return new File(RefData.class.getClassLoader().getResource(fileName).getFile());
    }

    private static void readCsvNew(File refFile) {
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
            LOGGER.error("Error occurred while transforming fm-lots.csv", e);
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
