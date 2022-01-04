package org.pra.nse.csv.read;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.csv.bean.in.FoBean;
import org.pra.nse.csv.bean.in.LsBean;
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

    public List<FoBean> read(String fileName) {
        List<FoBean> beanList;
        beanList = readCsv(fileName);
        return beanList;
    }

    private List<FoBean> readCsv(String fileName) {
        List<FoBean> beans = null;
        try {
//            csvFile = ResourceUtils.getFile("classpath:screens.csv");
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = mapper.schemaFor(FoBean.class).withHeader();
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
//        beans.forEach( row -> LOGGER.info("{}", row));
        return beans;
    }
}
