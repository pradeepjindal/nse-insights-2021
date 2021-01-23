package org.pra.nse.csv.data;

import org.pra.nse.ApCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CAO Csv Access Object
 */
public class MfiCaoNew {
    private static final Logger LOGGER = LoggerFactory.getLogger(MfiCaoNew.class);


    public static void saveOverWrite(String csvHeaderString,
                                     List<CalcBeanNew> beans,
                                     String toPath,
                                     Function<CalcBeanNew, String> csvStringFunction) {
        // create and collect csv lines
        List<String> csvLines = new ArrayList<>();
        //symbolMap.values().forEach( list -> list.forEach( dto -> csvLines.add(dto.toFullCsvString())));
        beans.forEach( bean -> csvLines.add( csvStringFunction.apply(bean) ));

        // print csv lines
        File csvOutputFile = new File(toPath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(csvHeaderString);
            csvLines.stream()
                    //.map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error: {}", e);
            throw new RuntimeException("mfi | could not create file: " + toPath);
        }
    }

    public static void saveAppend(List<MfiBeanNew> dtos) {

        //List<RsiEntity> rsiBeans0 = RsiData.load();
        String toPath = ApCo.ROOT_DIR + File.separator + ApCo.COMPUTE_DIR_NAME + File.separator + "mfi.csv";

        // create and collect csv lines
        List<String> csvLines = new ArrayList<>();
        dtos.forEach( bean -> csvLines.add(bean.toCsvString()));

        // print csv lines
        File csvOutputFile = new File(toPath);
        //overwrite mode
        //try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
        //append mode
        try (FileWriter fw = new FileWriter(csvOutputFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println("symbol, trade_date, AtpMfi05, AtpMfi10, AtpMfi15, AtpMfi20");
            csvLines.stream()
                    //.map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (IOException e) {
            LOGGER.error("Error: {}", e);
            throw new RuntimeException("mfi: Could not create file");
        }
    }

}
