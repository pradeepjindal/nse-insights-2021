package org.pra.nse;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class NseCons {

    //https://www.nseindia.com/content/historical/EQUITIES/2019/SEP/cm10SEP2019bhav.csv.zip
//    public static final String CM_BASE_URL = "https://www.nseindia.com/content/historical/EQUITIES";
//    public static final String FO_BASE_URL = "https://www.nseindia.com/content/historical/DERIVATIVES";

    //https://www1.nseindia.com/content/historical/EQUITIES/2019/SEP/cm10SEP2019bhav.csv.zip
    public static final String CM_BASE_URL = "https://www1.nseindia.com/content/historical/EQUITIES";
    public static final String FM_BASE_URL = "https://www1.nseindia.com/content/historical/DERIVATIVES";

    //https://archives.nseindia.com/content/historical/EQUITIES/2020/JAN/cm06JAN2020bhav.csv.zip
//    public static final String CM_BASE_URL = "https://archives.nseindia.com/content/historical/EQUITIES";
//    public static final String FO_BASE_URL = "https://archives.nseindia.com/content/historical/EQUITIES";


    //https://www.nseindia.com/archives/equities/mto/MTO_06012020.DAT
    //public static final String DM_BASE_URL = "https://www.nseindia.com/archives/equities/mto";
    //https://www1.nseindia.com/archives/equities/mto/MTO_06012020.DAT
    public static final String DM_BASE_URL = "https://www1.nseindia.com/archives/equities/mto";

    //https://www.nseindia.com/content/nsccl/fao_participant_vol_13092019.csv
    //public static final String BP_BASE_URL = "https://www.nseindia.com/content/nsccl";
    //https://www1.nseindia.com/content/nsccl/fao_participant_vol_13092019.csv
    public static final String BP_BASE_URL = "https://www1.nseindia.com/content/nsccl";

    //https://archives.nseindia.com/products/content/sec_bhavdata_full_06012020.csv
    public static final String DBC_BASE_URL = "https://archives.nseindia.com/products/content";


    public static final String NSE_FOLDER_NAME = "nse";
    public static final String NSE_DIR_PREFIX = NSE_FOLDER_NAME + "-";

    public static final String CM_DATA_NAME = "cm";
    public static final String DM_DATA_NAME = "dm";
    public static final String FM_DATA_NAME = "fm";
    public static final String EQ_DATA_NAME = "eq";
    public static final String IDX_DATA_NAME = "nx";

    public static final String CM_DIR_NAME = NSE_DIR_PREFIX + CM_DATA_NAME;
    public static final String DM_DIR_NAME = NSE_DIR_PREFIX + DM_DATA_NAME;
    public static final String FM_DIR_NAME = NSE_DIR_PREFIX + FM_DATA_NAME;
    public static final String EQ_DIR_NAME = NSE_DIR_PREFIX + EQ_DATA_NAME;
    public static final String IDX_DIR_NAME = NSE_DIR_PREFIX + IDX_DATA_NAME;

    public static final String CM_FILE_PREFIX = CM_DATA_NAME + "-";
    public static final String DM_FILE_PREFIX = DM_DATA_NAME + "-";
    public static final String FM_FILE_PREFIX = FM_DATA_NAME + "-";

    public static final String CM_FILES_PATH = ApCo.ROOT_DIR + File.separator + CM_DIR_NAME;
    public static final String DM_FILES_PATH = ApCo.ROOT_DIR + File.separator + DM_DIR_NAME;
    public static final String FM_FILES_PATH = ApCo.ROOT_DIR + File.separator + FM_DIR_NAME;
    public static final String IDX_FILES_PATH = ApCo.ROOT_DIR + File.separator + IDX_DIR_NAME;

    public static final String BP_DIR_NAME = "nse-bp";
    public static final String AB_DIR_NAME = "nse-ab"; //amiBroker
    public static final String DBC_DIR_NAME = "nse-dbc"; //dailyBhavCopy



    public static final String CM_FILE_NAME_REGEX = "";
    public static final String FM_FILE_NAME_REGEX = "";
    public static final String DM_FILE_NAME_REGEX = "";
    public static final String AB_FILE_NAME_REGEX = "";
    public static final String DBC_FILE_NAME_REGEX = "";

    public static final String ddMMMyyyy_UPPER_CASE_DATE_REGEX = "\\d{2}[A-Z]{3}\\d{4}";
    public static final String ddMMMyyyy_CAPITAL_CASE_DATE_REGEX = "\\d{2}[A-Z]{1}[a-z]{2}\\d{4}";
    public static final String ddMMyyyy_DATE_REGEX = "\\d{2}\\d{2}\\d{4}";
    public static final String NSE_CM_FILE_NAME_DATE_REGEX = ddMMMyyyy_UPPER_CASE_DATE_REGEX;
    public static final String NSE_FM_FILE_NAME_DATE_REGEX = ddMMMyyyy_UPPER_CASE_DATE_REGEX;
    public static final String NSE_DM_FILE_NAME_DATE_REGEX = ddMMyyyy_DATE_REGEX;
    public static final String NSE_BP_FILE_NAME_DATE_REGEX = ddMMyyyy_DATE_REGEX;
    public static final String NSE_DBC_FILE_NAME_DATE_REGEX = ddMMyyyy_DATE_REGEX;
    public static final String NSE_IDX_FILE_NAME_DATE_REGEX = ddMMyyyy_DATE_REGEX;

    public static final String AB_FILE_NAME_DATE_REGEX = ddMMMyyyy_CAPITAL_CASE_DATE_REGEX;


    public static final String NSE_CM_FILE_NAME_DATE_FORMAT = ApCo.ddMMMyyyy_DATE_FORMAT;
    public static final String NSE_FM_FILE_NAME_DATE_FORMAT = ApCo.ddMMMyyyy_DATE_FORMAT;
    public static final String NSE_DM_FILE_NAME_DATE_FORMAT = ApCo.ddMMyyyy_DATE_FORMAT;
    public static final String NSE_BP_FILE_NAME_DATE_FORMAT = ApCo.ddMMyyyy_DATE_FORMAT;
    public static final String NSE_DBC_FILE_NAME_DATE_FORMAT = ApCo.ddMMyyyy_DATE_FORMAT;
    public static final String NSE_IDX_FILE_NAME_DATE_FORMAT = ApCo.ddMMyyyy_DATE_FORMAT;

    public static final String AB_FILE_NAME_DATE_FORMAT = ApCo.ddMMMyyyy_DATE_FORMAT;


    public static final String NSE_CM_FILE_PREFIX = "cm";
    public static final String NSE_FM_FILE_PREFIX = "fo";
    public static final String NSE_DM_FILE_PREFIX = "MTO_";
    public static final String NSE_BP_FILE_PREFIX = "fao_participant_vol_";
    public static final String NSE_DBC_FILE_PREFIX = "sec_bhavdata_full_";
    public static final String NSE_IDX_FILE_PREFIX = "ind_close_all_";

    public static final String NSE_CM_FILE_SUFFIX = "bhav.csv";
    public static final String NSE_FM_FILE_SUFFIX = "bhav.csv";
    public static final String NSE_DM_FILE_SUFFIX = "";
    public static final String NSE_BP_FILE_SUFFIX = "";
    public static final String AB_FILE_SUFFIX = "";
    public static final String NSE_DBC_FILE_SUFFIX = "";

    public static final String NSE_CM_FILE_EXT = ".zip";
    public static final String NSE_FM_FILE_EXT = ".zip";
    public static final String NSE_DM_FILE_EXT = ".DAT";
    public static final String NSE_BP_FILE_EXT = ".csv";
    public static final String NSE_DBC_FILE_EXT = ".csv";
    public static final String AB_FILE_EXT = ".txt";
    public static final String NSE_IDX_FILE_EXT = ".csv";

    public static final DateTimeFormatter CM_FILE_NAME_DTF = DateTimeFormatter.ofPattern(NSE_CM_FILE_NAME_DATE_FORMAT);
    public static final DateTimeFormatter FM_FILE_NAME_DTF = DateTimeFormatter.ofPattern(NSE_FM_FILE_NAME_DATE_FORMAT);
    public static final DateTimeFormatter DM_FILE_NAME_DTF = DateTimeFormatter.ofPattern(NSE_DM_FILE_NAME_DATE_FORMAT);
    public static final DateTimeFormatter DBC_FILE_NAME_DTF = DateTimeFormatter.ofPattern(NSE_DBC_FILE_NAME_DATE_FORMAT);
    public static final DateTimeFormatter AB_FILE_NAME_DTF = DateTimeFormatter.ofPattern(AB_FILE_NAME_DATE_FORMAT);
    public static final DateTimeFormatter AB_DATA_DTF = DateTimeFormatter.ofPattern(ApCo.AB_DATA_DATE_FORMAT);

}
