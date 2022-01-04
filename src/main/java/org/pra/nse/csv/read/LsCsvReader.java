package org.pra.nse.csv.read;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.csv.bean.in.LsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class LsCsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LsCsvReader.class);

    public List<LsBean> read(String fromFile) {
        List<LsBean> loBeans = readCsv(new File(fromFile));
        return loBeans;
    }

    private List<LsBean> readCsv(File csvFile) {
        List<LsBean> beans = null;
        try {
            //csvFile = ResourceUtils.getFile("classpath:screens.csv");
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            CsvSchema schema = mapper.schemaFor(LsBean.class).withHeader();
            MappingIterator<LsBean> it = mapper.readerFor(LsBean.class).with(schema).readValues(csvFile);
            return it.readAll();
//            beans = new ArrayList<>();
//            while (it.hasNextValue()) {
//                LsBean fmBean = it.nextValue();
//                beans.add(fmBean);
//            }
//            LOGGER.info("CSV, Total Rows Count: [{}]", beans.size());
        } catch (IOException e) {
            LOGGER.error("Error occurred while loading object list from file:", e);
            new RuntimeException("error reading fm file");
        }
        return beans;
    }
}
