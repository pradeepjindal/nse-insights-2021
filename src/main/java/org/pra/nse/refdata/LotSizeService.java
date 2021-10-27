package org.pra.nse.refdata;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class LotSizeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotSizeService.class);
    private static final String fileName = "pra-top-70-lots.csv";
    private static List<LotSizeBean> lotSizeBeanList;
    private static Map<String, Long> lotSizeBeanMap;

    private static Map<String, List<LotSizeBean>> fileNameKey_lotSizeBeanList = new HashMap<>();
    private static Map<String, Map<String, Long>> fileNameKey_lotSizeBeanMap = new HashMap<>();

    private static EnumMap<FmCategoryEnum, String> enumMap = new EnumMap<>(FmCategoryEnum.class);

    static {
        enumMap.put(FmCategoryEnum.FM_ALL   , "fm-lots.csv");
        enumMap.put(FmCategoryEnum.FM_TOP_70, "pra-top-70-lots.csv");
        enumMap.put(FmCategoryEnum.FM_TOP_35, "pra-top-35-lots.csv");
    }

    public static long getLotSizeAsLong(String symbol) {
        String fileName = enumMap.get(FmCategoryEnum.FM_ALL);
        if(fileNameKey_lotSizeBeanMap.get(fileName) == null) {
            File refFile = readFile("data/" + fileName);
            readCsv2(fileName, refFile);
        }

        Map<String, Long> lotSizeBeanMap = fileNameKey_lotSizeBeanMap.get(fileName);
        return extractLostSize(lotSizeBeanMap, symbol);
    }

    public static BigDecimal getLotSizeAsBigDecimal(FmCategoryEnum category, String symbol) {
        String fileName = enumMap.get(category);
        if(fileNameKey_lotSizeBeanMap.get(fileName) == null) {
            File refFile = readFile("data/" + fileName);
            readCsv2(fileName, refFile);
        }

        Map<String, Long> lotSizeBeanMap = fileNameKey_lotSizeBeanMap.get(fileName);
        return new BigDecimal(extractLostSize(lotSizeBeanMap, symbol));
    }

    public static Set<String> getSymbolSet(FmCategoryEnum category) {
        String fileName = enumMap.get(category);
        if(fileNameKey_lotSizeBeanMap.get(fileName) == null) {
            File refFile = readFile("data/" + fileName);
            readCsv2(fileName, refFile);
        }

        Map<String, Long> lotSizeBeanMap = fileNameKey_lotSizeBeanMap.get(fileName);
        return lotSizeBeanMap.keySet();
    }

    private static long extractLostSize(Map<String, Long> lotSizeBeanMap, String symbol) {
        if(lotSizeBeanMap == null || lotSizeBeanMap.size() == 0)
            return 0;
        else
            return lotSizeBeanMap.getOrDefault(symbol, 0L);
    }

    private static File readFile(String fileName) {
        return new File(LotSizeService.class.getClassLoader().getResource(fileName).getFile());
    }

    private static void readCsv2(String fileName, File refFile) {
        lotSizeBeanList = new ArrayList<>();
        lotSizeBeanMap = new HashMap<>();
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
                lotSizeBeanList.add(bean);
                lotSizeBeanMap.put(bean.getSymbol(), bean.getSize());
            }
            LOGGER.info("LotSize, Total Rows Count: [{}]", lotSizeBeanList.size());
            fileNameKey_lotSizeBeanMap.put(fileName, lotSizeBeanMap);
        } catch (Exception e) {
            LOGGER.error("Error occurred while transforming " + fileName, e);
            //return Collections.emptyList();
        }
        //lotSizeBeanList.forEach( row -> LOGGER.info("{}", row));
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
                lotSizeBeanList.add(bean);
                lotSizeBeanMap.put(bean.getSymbol(), bean.getSize());
            }
            LOGGER.info("CSV, Total Rows Count: [{}]", beans.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading object list from file " + filePath, e);
            //return Collections.emptyList();
        }
        beans.forEach( row -> LOGGER.info("{}", row));
    }

}
