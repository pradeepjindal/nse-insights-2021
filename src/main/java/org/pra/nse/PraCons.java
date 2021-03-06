package org.pra.nse;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class PraCons {

    public static final String PRA_FOLDER_NAME = "pra";
    public static final String PRA_DIR_PREFIX = PRA_FOLDER_NAME + "-";

    public static final String CM_DATA_NAME = "cm";
    public static final String DM_DATA_NAME = "dm";
    public static final String FM_DATA_NAME = "fm";
    public static final String EQ_DATA_NAME = "eq";
    public static final String IDX_DATA_NAME = "nx";

    public static final String CM_DIR_NAME = PRA_DIR_PREFIX + CM_DATA_NAME;
    public static final String DM_DIR_NAME = PRA_DIR_PREFIX + DM_DATA_NAME;
    public static final String FM_DIR_NAME = PRA_DIR_PREFIX + FM_DATA_NAME;
    public static final String EQ_DIR_NAME = PRA_DIR_PREFIX + EQ_DATA_NAME;
    public static final String IDX_DIR_NAME = PRA_DIR_PREFIX + IDX_DATA_NAME;

    public static final String CM_FILE_PREFIX = CM_DATA_NAME + "-";
    public static final String DM_FILE_PREFIX = DM_DATA_NAME + "-";
    public static final String FM_FILE_PREFIX = FM_DATA_NAME + "-";

    public static final String CM_FILES_PATH = ApCo.ROOT_DIR + File.separator + CM_DIR_NAME;
    public static final String DM_FILES_PATH = ApCo.ROOT_DIR + File.separator + DM_DIR_NAME;
    public static final String FM_FILES_PATH = ApCo.ROOT_DIR + File.separator + FM_DIR_NAME;
    public static final String IDX_FILES_PATH = ApCo.ROOT_DIR + File.separator + IDX_DIR_NAME;


}
