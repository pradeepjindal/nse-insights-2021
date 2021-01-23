package org.pra.nse.csv.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CAO Csv Access Object
 */
public class AvgCaoNew {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvgCaoNew.class);


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
            throw new RuntimeException("avg | could not create file: " + toPath);
        }
    }

}
