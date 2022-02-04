package org.pra.nse.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    public static String constructFileName(LocalDate localDate, String fileNameDateFormat, String filePrefix, String fileSuffix, String fileExtension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fileNameDateFormat);
        String newFileName = filePrefix + formatter.format(localDate).toUpperCase() + fileSuffix + fileExtension;
        return newFileName;
    }


}
