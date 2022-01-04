package org.pra.nse.csv.read;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.csv.bean.in.FoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

@Component
public class FoCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoCsvReader.class);

    public Map<String, Map<LocalDate, FoBean>> read(String fileName) {
        Map<String, Map<LocalDate, FoBean>> localFoBeanMap = new HashMap<>();
        LOGGER.info("-----CSV Reader");

        FoBean fmBean;
        //int missing = 0;
        Map.Entry<String, Integer> missingEntry = new AbstractMap.SimpleEntry<>("missing", 0);
        List<FoBean> fmBeanList = new ArrayList<>();

        fmBeanList = readCsv(fileName);

        fmBeanList.stream().forEach( bean->{
            localFoBeanMap.put(bean.getSymbol(), null);
        });
//            if(foBeanMap == null) {
//                LOGGER.info("Total Beans in Map: " + localFoBeanMap.size());
//                LOGGER.info("Total Data Rows : " + (beanReader.getRowNumber()-1));
//                LOGGER.info("Total Map Rows : " + (localFoBeanMap.size()));
//                LOGGER.info("Does all rows from csv accounted for ? : " + (beanReader.getRowNumber()-1 ==  localFoBeanMap.size() ? "Yes" : "No"));
//            } else {
//                LOGGER.info("Total Beans in Map: " + foBeanMap.size());
//                LOGGER.info("Total Data Rows : " + (beanReader.getRowNumber()-1));
//                LOGGER.info("Total Map Rows : " + (foBeanMap.size()));
//                LOGGER.info("Does all rows from csv accounted for ? : " + (beanReader.getRowNumber()-1 ==  foBeanMap.size() ? "Yes" : "No"));
//            }
        return localFoBeanMap;
    }

    private List<FoBean> readCsv(String fileName) {
        List<FoBean> beans = null;
        try {
//            csvFile = ResourceUtils.getFile("classpath:screens.csv");
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(FoBean.class);
            MappingIterator<FoBean> it = mapper.readerFor(FoBean.class).with(schema).readValues(new File(fileName));
            //return it.readAll();
            beans = new ArrayList<>();
            while (it.hasNextValue()) {
                beans.add(it.nextValue());
            }
            LOGGER.info("CSV, Total Rows Count: [{}]", beans.size());
        } catch(FileNotFoundException fnfe) {
            LOGGER.error("Error: ", fnfe);
        } catch (Exception ex) {
            LOGGER.error("Error:", ex);
            //return Collections.emptyList();
        }
        beans.forEach( row -> LOGGER.info("{}", row));
        return beans;
    }
}
