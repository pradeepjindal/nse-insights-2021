package org.pra.nse;

import java.io.File;

public class ProCo {

    public static String outputPathAndFileNameForFixFile(String processFileName) {
        return outputPathAndFileNameForFixFile(processFileName, null);
    }
    public static String outputPathAndFileNameForFixFile(String processFileName, String fileSuffix) {
        String outputPathAndFileName = ApCo.ROOT_DIR
                + File.separator + ApCo.REPORTS_DIR_NAME
                + File.separator + processFileName;
        return addSuffix(outputPathAndFileName, fileSuffix);
    }

    public static String outputPathAndFileNameForDynamicFile(String processFileName, String foLatestFileName) {
        return outputPathAndFileNameForDynamicFile(processFileName, foLatestFileName, null);
    }
    public static String outputPathAndFileNameForDynamicFile(String processFileName, String foLatestFileName, String fileSuffix) {
        String fileDate = extractDate(foLatestFileName);
        String outputPathAndFileName = ApCo.ROOT_DIR
                + File.separator + ApCo.REPORTS_DIR_NAME
                + File.separator + processFileName + "-" + fileDate;
        return addSuffix(outputPathAndFileName, fileSuffix);
    }

    private static String addSuffix(String outputPathAndFileName, String fileSuffix) {
        if(null == fileSuffix) {
            return outputPathAndFileName + ApCo.REPORTS_FILE_EXT;
        } else {
            return outputPathAndFileName + fileSuffix + ApCo.REPORTS_FILE_EXT;
        }
    }

    public static String extractDate(String fileName) {
        return fileName == null ? null : fileName.substring(fileName.lastIndexOf(".")-10, fileName.lastIndexOf("."));
    }

}
