package org.pra.nse.util;

import org.pra.nse.ApCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DirUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirUtils.class);

    public void createFolders(String outputPathAndFileName) {
        //TODO at the beginning of program, check all the folders required and create if missing
    }

    public static void createRootFolder() {
        String dataDir = ApCo.ROOT_DIR;
        ensureDirPath(dataDir);
    }

    public static void createReportFolder(String outputPathAndFileName) {
        String dataDir = ApCo.ROOT_DIR + File.separator + ApCo.REPORTS_DIR_NAME;
        ensureDirPath(dataDir);
    }

    public static void ensureFolder(String folderName) {
        String path = ApCo.ROOT_DIR + File.separator + folderName;
        ensureDirPath(path);
    }

    public static void ensureDirPath(String folderNameWithPath) {
        File folder = new File(folderNameWithPath);
        if(null == folder.listFiles()) {
            createFolder(folder);
        } else {
            LOGGER.info("dir | exists ({})", folderNameWithPath);
        }
    }


    public static void createFolder(File newDir) {
        boolean created =  newDir.mkdir();
        if(created)
            LOGGER.info("dir creation | succeed ({})", newDir);
        else
            LOGGER.error("dir creation | failed ({})", newDir);
    }

}
