package org.pra.nse.calculation;

import org.pra.nse.ApCo;

import java.io.File;

public class CalcCons {

    public static final String CALC_FOLDER_NAME = "calc";
    public static final String CALC_DIR_PREFIX = CALC_FOLDER_NAME + "-";

    public static final String AVG_DATA_NAME = "avg";
    public static final String MFI_DATA_NAME = "mfi";
    public static final String RSI_DATA_NAME = "rsi";


    public static final String AVG_DIR_NAME = CALC_DIR_PREFIX + AVG_DATA_NAME;
    public static final String MFI_DIR_NAME = CALC_DIR_PREFIX + MFI_DATA_NAME;
    public static final String RSI_DIR_NAME = CALC_DIR_PREFIX + RSI_DATA_NAME;

    public static final String AVG_DIR_NAME_NEW = CALC_DIR_PREFIX + AVG_DATA_NAME + "-new";
    public static final String MFI_DIR_NAME_NEW = CALC_DIR_PREFIX + MFI_DATA_NAME + "-new";
    public static final String RSI_DIR_NAME_NEW = CALC_DIR_PREFIX + RSI_DATA_NAME + "-new";

    public static final String AVG_FILE_PREFIX = AVG_DATA_NAME + "-";
    public static final String MFI_FILE_PREFIX = MFI_DATA_NAME + "-";
    public static final String RSI_FILE_PREFIX = RSI_DATA_NAME + "-";

    public static final String AVG_FILES_PATH = ApCo.ROOT_DIR +File.separator+ AVG_DIR_NAME;
    public static final String MFI_FILES_PATH = ApCo.ROOT_DIR +File.separator+ MFI_DIR_NAME;
    public static final String RSI_FILES_PATH = ApCo.ROOT_DIR +File.separator+ RSI_DIR_NAME;

    public static final String AVG_FILES_PATH_NEW = ApCo.ROOT_DIR +File.separator+ AVG_DIR_NAME_NEW;
    public static final String MFI_FILES_PATH_NEW = ApCo.ROOT_DIR +File.separator+ MFI_DIR_NAME_NEW;
    public static final String RSI_FILES_PATH_NEW = ApCo.ROOT_DIR +File.separator+ RSI_DIR_NAME_NEW;

    public static final String AVG_CSV_HEADER
            = "symbol,trade_date," +
            "atpAvg03,atpAvg05,atpAvg10,atpAvg15,atpAvg20," +
            "volAvg03,volAvg05,volAvg10,volAvg15,volAvg20," +
            "delAvg03,delAvg05,delAvg10,delAvg15,delAvg20," +
            "oiAvg03,oiAvg05,oiAvg10,oiAvg15,oiAvg20";
    public static final String RSI_CSV_HEADER
            = "symbol,trade_date," +
            "OpenRsi03,HighRsi03,LowRsi03,CloseRsi03,LastRsi03,AtpRsi03,HlmRsi05,ohlc03," +
            "OpenRsi05,HighRsi05,LowRsi05,CloseRsi05,LastRsi05,AtpRsi05,HlmRsi05,ohlc05," +
            "OpenRsi10,HighRsi10,LowRsi10,CloseRsi10,LastRsi10,AtpRsi10,HlmRsi10,ohlc10," +
            "OpenRsi20,HighRsi20,LowRsi20,CloseRsi20,LastRsi20,AtpRsi20,HlmRsi20,ohlc20";
    public static final String MFI_CSV_HEADER
            = "symbol,trade_date," +
            "volAtpMfi03,volAtpMfi05,volAtpMfi10,volAtpMfi15,volAtpMfi20," +
            "delAtpMfi03,delAtpMfi05,delAtpMfi10,delAtpMfi15,delAtpMfi20";

    public static final String AVG_CSV_HEADER_NEW
            = "symbol,tradeDate,forDays," +
            "atpAvgSma,volAvgSma,delAvgSma,foiAvgSma";
    public static final String MFI_CSV_HEADER_NEW
            = "symbol,tradeDate,forDays," +
            "volAtp_MfiSma,delAtp_MfiSma";
    public static final String RSI_CSV_HEADER_NEW
            = "symbol,tradeDate,forDays," +
            "OpenRsiSma,HighRsiSma,LowRsiSma,CloseRsiSma,LastRsiSma,AtpRsiSma,HlmRsiSma,ohlcRsiSma,delRsiSma";
}
