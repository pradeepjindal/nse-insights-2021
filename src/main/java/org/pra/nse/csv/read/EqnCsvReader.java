package org.pra.nse.csv.read;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.csv.bean.in.EqnBean;
import org.pra.nse.csv.bean.in.FmBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EqnCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EqnCsvReader.class);

    public Map<FmBean, FmBean> read(Map<FmBean, FmBean> foBeanMap, String fileName) throws FileNotFoundException {
        Map<FmBean, FmBean> localFoBeanMap = new HashMap<>();
        LOGGER.info("-----CSV Reader [{}]", fileName);

        FmBean fmBean;
        //int missing = 0;
        AtomicInteger missingEntry = new AtomicInteger();
        List<FmBean> fmBeanList = new ArrayList<>();

        //foBeanList = readCsv(new File(fileName));

        fmBeanList.stream().forEach(bean->{
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

    public List<EqnBean> readCsv(File csvFile) {
        csvFile = new File("C:\\Users\\prajinda\\pra-nse-computed-data\\marutieqn.csv");
        List<EqnBean> beans = null;
        try {
            //csvFile = ResourceUtils.getFile("classpath:screens.csv");
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = mapper.schemaFor(EqnBean.class).withHeader();
            MappingIterator<EqnBean> it = mapper.readerFor(EqnBean.class).with(schema).readValues(csvFile);
            //return it.readAll();
            beans = new LinkedList<>();
            while (it.hasNextValue()) {
                beans.add(it.nextValue());
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
