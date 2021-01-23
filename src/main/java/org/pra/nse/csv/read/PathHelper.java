package org.pra.nse.csv.read;

import org.pra.nse.ApCo;

import java.io.File;

public class PathHelper {
    public static String fileNameWithFullPath(String dirName, String filePrefix, String fromFile) {
        int firstIndex = fromFile.lastIndexOf(filePrefix);
        String cmCsvFileName = fromFile.substring(firstIndex, firstIndex+13) + ApCo.REPORTS_FILE_EXT;
        String toFile = ApCo.ROOT_DIR + File.separator + dirName + File.separator + cmCsvFileName;
        return toFile;
    }
}
