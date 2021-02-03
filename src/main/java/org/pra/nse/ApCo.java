package org.pra.nse;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ApCo {

    public static final LocalTime DAILY_DOWNLOAD_TIME = LocalTime.of(18,0,0,0);
    //
    //public static final LocalDate DOWNLOAD_FROM_DATE    = LocalDate.of(2020,7,25);
    public static final LocalDate DOWNLOAD_NSE_FROM_DATE = LocalDate.of(2021,2,1);
    public static final LocalDate UPLOAD_NSE_FROM_DATE  = LocalDate.of(2021,2,1);

    public static final LocalDate CALC_FROM_DATE_NEW    = LocalDate.of(2021,2,1);
    public static final LocalDate UPLOAD_CALC_FROM_DATE = LocalDate.of(2021,2,1);

    public static final LocalDate REPORTS_FROM_DATE     = LocalDate.of(2021,2,1);
    public static final LocalDate EMAIL_FROM_DATE       = LocalDate.of(2021,2,1);


    public static final boolean EMAIL_ENABLED = false;
    public static final boolean EMAIL_ENABLED_FOR_MANISH = true;
    public static final boolean EMAIL_ENABLED_FOR_PRADEEP = false;
    public static final boolean EMAIL_ENABLED_FOR_SHUVI = false;

    //public static final String ROOT_DIR = System.getProperty("user.home");
    //public static final String ROOT_DIR = "d:" + File.separator + "nseHome";
    //public static final String ROOT_DIR = System.getProperty("user.home");
    public static final String ROOT_DIR = "d:" +File.separator+ "nseEnv-2021" +File.separator+ "nse-data";

    public static final String AVG_DIR_NAME = "calc-avg";
    public static final String RSI_DIR_NAME = "calc-rsi";
    public static final String MFI_DIR_NAME = "calc-mfi";

        public static final String COMPUTE_DIR_NAME = "computed-data";

        public static final String REPORTS_DIR_NAME = "reports-data";
        public static final String REPORTS_DIR_NAME_TMP = "reports-tmp";
        public static final String REPORTS_DIR_NAME_DSR = "reports-dsr";

//    public static final String REPORTS_DIR_NAME_PPF = "reports-ppf";
    public static final String REPORTS_DIR_NAME_PPF = "reports-ppf-tmp";
    public static final String REPORTS_DIR_NAME_PPF_NEW = "reports-ppf-new";

        public static final String REPORTS_DIR_NAME_MANISH = "reports-manish";
        public static final String REPORTS_DIR_NAME_SHUVI = "reports-shuvi";

    public static final String PRADEEP_DIR_NAME = "reports-pradeep";
    public static final String MANISH_DIR_NAME = "reports-manish";
    public static final String SHUVI_DIR_NAME = "reports-shuvi";


    public static final String AB_FILE_PREFIX = "EQ_";
    public static final String PRA_CM_FILE_PREFIX = "cm-";
    public static final String PRA_FM_FILE_PREFIX = "fm-";
    public static final String PRA_DM_FILE_PREFIX = "dm-";
    public static final String PRA_BP_FILE_PREFIX = "bp-";


    public static final String DATE_REGEX_yyyyMMdd = "\\d{4}-\\d{2}-\\d{2}";
    public static final String DATA_FILE_NAME_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String PRA_FILE_NAME_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
    public static final String DEFAULT_FILE_NAME_DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";

    public static final String ddMMMyyyy_DATE_FORMAT = "ddMMMyyyy";
    public static final String ddMMyyyy_DATE_FORMAT = "ddMMyyyy";
    public static final String yyyyMMdd_DATE_FORMAT = "yyyyMMdd";
    public static final String yyyy_D_MM_D_dd_DATE_FORMAT = "yyyy-MM-dd";
    public static final String dd_D_MMM_D_yyyy_DATE_FORMAT = "dd-MMM-yyyy";

    //DateTimeFormatter.ISO_LOCAL_DATE;
    public static final String DATA_FILE_NAME_DATE_FORMAT = yyyy_D_MM_D_dd_DATE_FORMAT;
    public static final String PRA_FILE_NAME_DATE_FORMAT = yyyy_D_MM_D_dd_DATE_FORMAT;
    public static final String DEFAULT_FILE_NAME_DATE_FORMAT = yyyy_D_MM_D_dd_DATE_FORMAT;

    public static final String PRA_CM_DATA_DATE_FORMAT = dd_D_MMM_D_yyyy_DATE_FORMAT;
    public static final String PRA_FM_DATA_DATE_FORMAT = dd_D_MMM_D_yyyy_DATE_FORMAT;
    public static final String PRA_DM_DATA_DATE_FORMAT = yyyy_D_MM_D_dd_DATE_FORMAT;
    public static final String AB_DATA_DATE_FORMAT = yyyyMMdd_DATE_FORMAT;
    public static final String REPORTS_DATE_FORMAT = dd_D_MMM_D_yyyy_DATE_FORMAT;
    public static final String FIX_HOLIDAYS_DATE_FORMAT = "dd-MMM";


    public static final String DEFAULT_FILE_EXT = ".csv";
    public static final String DATA_FILE_EXT = DEFAULT_FILE_EXT;
    public static final String REPORTS_FILE_EXT = DEFAULT_FILE_EXT;


    public static final String PRADEEP_FILE_NAME = "pradeepData";
    public static final String SHUVI_FILE_NAME = "shuviData";
    public static final String MANISH_FILE_NAME = "manishData";
    public static final String MANISH_FILE_NAME_B = "manishDataB";

    //public static final String REPORTS_PATH = ROOT_DIR + File.separator + REPORTS_DIR_NAME


    public static final DateTimeFormatter ddMMMyyyy_DTF = DateTimeFormatter.ofPattern(ApCo.ddMMMyyyy_DATE_FORMAT);
    public static final DateTimeFormatter ddMMyyyy_DTF = DateTimeFormatter.ofPattern(ApCo.ddMMyyyy_DATE_FORMAT);
    public static final DateTimeFormatter yyyyMMdd_DTF = DateTimeFormatter.ofPattern(ApCo.yyyyMMdd_DATE_FORMAT);

    public static final DateTimeFormatter DATA_FILE_NAME_DTF = DateTimeFormatter.ofPattern(ApCo.DATA_FILE_NAME_DATE_FORMAT);


    public static final DateTimeFormatter PRA_DTF = DateTimeFormatter.ofPattern(ApCo.PRA_FILE_NAME_DATE_FORMAT);
    public static final String PRA_FILES_PATH = ApCo.ROOT_DIR + File.separator + ApCo.REPORTS_DIR_NAME;

    public static final DateTimeFormatter DEFAULT_DTF = DateTimeFormatter.ofPattern(ApCo.DEFAULT_FILE_NAME_DATE_FORMAT);
    public static final String DEFAULT_FILES_PATH = ApCo.ROOT_DIR + File.separator + ApCo.ROOT_DIR;

    public static final DateTimeFormatter FIX_HOLIDAYS_DTF = DateTimeFormatter.ofPattern(ApCo.FIX_HOLIDAYS_DATE_FORMAT);

}
