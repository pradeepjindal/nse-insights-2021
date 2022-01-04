package org.pra.nse.refdata;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class ExpiryDateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiryDateService.class);
    private static final String fileName = "fm-expiry-dates.csv";
    private static Map<LocalDate, LocalDate> expiryDateMap;


    static {
        File refFile = readFile("data/" + fileName);
        readCsv2(fileName, refFile);
    }

    public LocalDate getExpiryDateForMonth(LocalDate fixExpiryDate) {
        return expiryDateMap.get(fixExpiryDate);
    }


    private static File readFile(String fileName) {
        return new File(ExpiryDateService.class.getClassLoader().getResource(fileName).getFile());
    }

    private static void readCsv2(String fileName, File refFile) {
        try {
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            //TODO put this file into resource folder
            MappingIterator<ExpiryDateBean> it = mapper.readerFor(ExpiryDateBean.class).with(schema).readValues(refFile);
            //return it.readAll();
            ExpiryDateBean bean = null;
            while (it.hasNextValue()) {
                bean = it.nextValue();
                //LOGGER.info("{}", bean);
                expiryDateMap.put(bean.getFed(), bean.getEd());
            }
            LOGGER.info("ExpiryDates, Total Rows Count: [{}]", expiryDateMap.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred while transforming " + fileName, e);
            //return Collections.emptyList();
        }
        //lotSizeBeanList.forEach( row -> LOGGER.info("{}", row));
    }

}
