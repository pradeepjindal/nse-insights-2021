package org.pra.nse.csv.read;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.csv.bean.in.FmBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FmCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmCsvReader.class);

    public Map<FmBean, FmBean> read(Map<FmBean, FmBean> foBeanMap, String fileName) {
        Map<FmBean, FmBean> localFoBeanMap = new HashMap<>();
        LOGGER.info("FO file : [{}]", fileName);

        FmBean fmBean;
        //int missing = 0;
        AtomicInteger missingEntry = new AtomicInteger();
        List<FmBean> fmBeanList = new ArrayList<>();

        fmBeanList = readCsv(new File(fileName));

        fmBeanList.stream().forEach(bean-> {
            if( null == foBeanMap) {
                localFoBeanMap.put(bean, bean);
            } else {
                if(foBeanMap.containsKey(bean)) {
                    FmBean fmBean1 = foBeanMap.get(bean);
                    foBeanMap.put(fmBean1, bean);
                } else {
                    //LOGGER.info("bean not found: " + foBean);
                    missingEntry.incrementAndGet();
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
            //csvFile = ResourceUtils.getFile("classpath:screens.csv");
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = mapper.schemaFor(FmBean.class).withHeader();
            MappingIterator<FmBean> it = mapper.readerFor(FmBean.class).with(schema).readValues(csvFile);
            //return it.readAll();
            beans = new ArrayList<>();
            LOGGER.warn("OPTIDX and OPTSTK are disbled, hence would not be loaded");
            while (it.hasNextValue()) {
                FmBean fmBean = it.nextValue();
                if("FUTSTK".equals(fmBean.getInstrument().trim()) || "FUTIDX".equals(fmBean.getInstrument().trim())) {
                    beans.add(fmBean);
                }
            }
            LOGGER.info("CSV, Total Rows Count: [{}]", beans.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading object list from file: {}", e);
            //return Collections.emptyList();
        }
        //System.out.println("TOTAL beans read=" + beans.size());
        //beans.forEach( row -> LOGGER.info("{}", row));
        return beans;
    }
}
