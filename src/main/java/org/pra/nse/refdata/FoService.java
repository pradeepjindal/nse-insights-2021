package org.pra.nse.refdata;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.pra.nse.ApCo;
import org.pra.nse.csv.bean.in.FoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class FoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoService.class);

    private static final String foDirName = ApCo.FO_DIR_NAME;
    private static final String foFilePrefix = ApCo.PRA_FO_FILE_PREFIX;
    private static final String foFileExtension = ApCo.CSV_FILE_EXT;
    private static final String DataDir = ApCo.ROOT_DIR + File.separator + foDirName;

//    private Map<String, String>

    public static int getLotSize(String symbol, LocalDate tradeDate, LocalDate expiryDate) {
        readCsvAsRawLines();

        String fileName = foFilePrefix + tradeDate.toString() + foFileExtension;
        String source = DataDir + File.separator + fileName;
        readCsv2(fileName, new File(source));
        return 1;
    }

    private static File readFile(String fileName) {
        return new File(FoService.class.getClassLoader().getResource(fileName).getFile());
    }

    private static void readCsvAsRawLines() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\nseEnv-2021\\nse-data\\nse-fo\\fo01012015.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        records.remove(0);
        records.remove(records.size()-1);

        String instrument;
        String symbol;
        String expiry;
        String value;
        String quantity;
        String contracts;
        //
        String instrumentString;
        String symbolString;
        LocalDate expiryLocalDate;
        BigDecimal valueBigDecimal;
        int quantityInt;
        int contractsInt;
        int lotSize;

        Set<LocalDate> dates = new HashSet<>();
        for(List<String> raw: records) {
            instrument = raw.get(0).trim();
            symbol = raw.get(1).trim();
            expiry = raw.get(2).trim();
            value = raw.get(8).trim();
            quantity = raw.get(9).trim();
            contracts = raw.get(10).trim();
            //
            instrumentString = instrument;
            symbolString = symbol;
            String[] expiryArray = expiry.split("/");
            expiryLocalDate = LocalDate.of(
                    Integer.valueOf(expiryArray[2]),
                    Integer.valueOf(expiryArray[1]),
                    Integer.valueOf(expiryArray[0])
            );
            dates.add(expiryLocalDate);
            //
            valueBigDecimal = new BigDecimal(value);
            quantityInt = Integer.valueOf(quantity);
            contractsInt = Integer.valueOf(contracts);
            lotSize = quantityInt / contractsInt;
            LOGGER.info("{}, {}, {}", symbolString, lotSize, expiryLocalDate);
        }
        LOGGER.info("{}", dates);
    }


    private static void readCsv2(String fileName, File refFile) {
        try {
            CsvMapper mapper = new CsvMapper();
            mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
//            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            //TODO put this file into resource folder
            MappingIterator<FoBean> it = mapper.readerFor(FoBean.class).with(schema).readValues(refFile);
            //return it.readAll();
            FoBean bean = null;
            while (it.hasNextValue()) {
                bean = it.nextValue();
                LOGGER.info("{}", bean);
//                expiryDateMap.put(bean.getFed(), bean.getEd());
            }
            LOGGER.info("ExpiryDates, Total Rows Count: [{}]", 1);
        } catch (Exception e) {
            LOGGER.error("Error occurred while transforming " + fileName, e);
            //return Collections.emptyList();
        }
        //lotSizeBeanList.forEach( row -> LOGGER.info("{}", row));
    }

}
