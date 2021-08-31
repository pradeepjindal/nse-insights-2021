package org.pra.nse.refdata;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class IdxFiveHundredService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdxFiveHundredService.class);

    private static Map<String, Map<String, IdxFiveHundredBean>> fileNameKey_idxSymbolMap = new HashMap<>();

    private static EnumMap<IdxCategoryEnum, String> enumMap = new EnumMap<IdxCategoryEnum, String>(IdxCategoryEnum.class);

    static {
        enumMap.put(IdxCategoryEnum.IDX_500, "idx-nifty-500.csv");
    }

    public static boolean isMember(IdxCategoryEnum category, String symbol) {
        String fileName = enumMap.get(category);
        if(fileNameKey_idxSymbolMap.get(fileName) == null) {
            File refFile = readFile("data/" + fileName);
            readCsv2(fileName, refFile);
        }

        Map<String, IdxFiveHundredBean> idxSymbolMap = fileNameKey_idxSymbolMap.get(fileName);
        return idxSymbolMap.containsKey(symbol);
    }

    public static boolean isNotMember(IdxCategoryEnum category, String symbol) {
        String fileName = enumMap.get(category);
        if(fileNameKey_idxSymbolMap.get(fileName) == null) {
            File refFile = readFile("data/" + fileName);
            readCsv2(fileName, refFile);
        }

        Map<String, IdxFiveHundredBean> idxSymbolMap = fileNameKey_idxSymbolMap.get(fileName);
        return !idxSymbolMap.containsKey(symbol);
    }

    private static File readFile(String fileName) {
        return new File(IdxFiveHundredService.class.getClassLoader().getResource(fileName).getFile());
    }

    private static void readCsv2(String fileName, File refFile) {
        Map<String, IdxFiveHundredBean> idxSymbolMap = new HashMap<>();
        try {
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            //TODO put this file into resource folder
            MappingIterator<IdxFiveHundredBean> it = mapper.readerFor(IdxFiveHundredBean.class).with(schema).readValues(refFile);
            //return it.readAll();
            IdxFiveHundredBean bean = null;
            while (it.hasNextValue()) {
                bean = it.nextValue();
                //LOGGER.info("{}", bean);
                idxSymbolMap.put(bean.getSymbol(), bean);
            }
            LOGGER.info("Idx500, Total Rows Count: [{}]", idxSymbolMap.size());
            fileNameKey_idxSymbolMap.put(fileName, idxSymbolMap);
        } catch (Exception e) {
            LOGGER.error("Error occurred while transforming " + fileName, e);
            //return Collections.emptyList();
        }
    }

}
