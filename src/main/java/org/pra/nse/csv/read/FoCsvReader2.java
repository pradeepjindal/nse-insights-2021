package org.pra.nse.csv.read;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.csv.bean.in.FmBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FoCsvReader2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoCsvReader2.class);

    public Map<FmBean, FmBean> read(Map<FmBean, FmBean> foBeanMap, String fileName) throws FileNotFoundException {
        Map<FmBean, FmBean> localFoBeanMap = new HashMap<>();
        LOGGER.info("-----CSV Reader");


        FmBean fmBean;
        //int missing = 0;
        Map.Entry<String, Integer> missingEntry = new AbstractMap.SimpleEntry<>("missing", 0);
        List<FmBean> fmBeanList = new ArrayList<>();

        fmBeanList = readCsv(new File(fileName));

        fmBeanList.stream().forEach(bean->{
            if( null == foBeanMap) {
                localFoBeanMap.put(bean, bean);
            } else {
                if(foBeanMap.containsKey(bean)) {
                    FmBean fmBean1 = foBeanMap.get(bean);
                    foBeanMap.put(fmBean1, bean);
                } else {
                    //LOGGER.info("bean not found: " + foBean);
                    missingEntry.setValue(missingEntry.getValue()+1);
                }
            }
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
        return foBeanMap == null ? localFoBeanMap : foBeanMap;
    }

    private List<FmBean> readCsv(File csvFile) {
        List<FmBean> beans = null;
        try {
            csvFile = ResourceUtils.getFile("classpath:screens.csv");
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(FmBean.class);
            MappingIterator<FmBean> it = mapper.readerFor(FmBean.class).with(schema).readValues(csvFile);
            //return it.readAll();
            beans = new ArrayList<>();
            while (it.hasNextValue()) {
                beans.add(it.nextValue());
            }
            LOGGER.info("CSV, Total Rows Count: [{}]", beans.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading object list from file " + csvFile, e);
            //return Collections.emptyList();
        }
        beans.forEach( row -> LOGGER.info("{}", row));
        return beans;
    }
}
