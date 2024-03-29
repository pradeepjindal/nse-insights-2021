package org.pra.nse.report.pastPresentFuture;

import java.util.List;

public class ReportConstants {
    public static final String DSRD = "DeliverySpikeReportDaily";
    public static final String DSRF = "DeliverySpikeReportFull";

    public static final String PPF_10 = "PPF_10_Report";
    public static final String PPF_20 = "PPF_20_Report";
    public static final String PPF_FULL = "PPF_days_Full_Report";
    public static final String PPF = "PPF_days_Report";

    public static final String DMS = "DMS_Report";

    static final String DSRF_CSV_HEADER =
            "symbol,trade_date," +
            "open,high,low,close,last,atp,hlm," +
            "open_ChgPrcnt,high_ChgPrcnt,low_ChgPrcnt,close_ChgPrcnt,last_ChgPrcnt,atp_ChgPrcnt," +
            "traded_ChgPrcnt,delivered_ChgPrcnt,oiChgPrcnt,premium," +
            "othighPrcnt,otlowPrcnt,otclosePrcnt,otlastPrcnt,otatpPrcnt," +
            "tdyAtpRsi10Ema,tdyCloseRsi10Ema,tdyLastRsi10Ema";

    static final String abc = "Tally,Growth,Spike,Bell";

    static final String PPF_CSV_HEADER =
            "symbol,trade_date,day," +
//            "open,high,low,hlp,close,last,closingBell,closeToLastPct,atp,hlm,ohlc," +
            "open,high,low,close,last,atp,ohlc,hlm,hld,hlp,cToL_Pct," +

//            "calcAtpFixGrowth,calcAtpDynGrowth,atpChgPct," +
//            "calcVolFixGrowth,calcVolDynGrowth,volChgPct," +
//            "delTally,calcDelFixGrowth,calcDelDynGrowth,delChgPct," +
//            "calcFoiFixGrowth,calcFoiDynGrowth,foiChgPct," +
                    "calcAtpFixGrowth,atpChgPct," +
                    "calcVolFixGrowth,volChgPct," +
                    "vdr,sumDel," +
                    "calcDelFixGrowth,delChgPct," +
                    "calcFoiFixGrowth,foiChgPct," +

            "premium,openingBell," +
            "nxtCloseToOpenPct,nxtOptoHighPct,nxtOptoLowPct,nxtOptoAtpPct," +
            "VolAtpMfi,DelAtpMfi,DelAtpMfiChg," +
            "AtpRsi,AtpRsiChg,CloseRsi,LastRsi,lotSize";

    static final String PPF_CSV_HEADER_2 =
            "symbol,trade_date,day," +
//            "open,high,low,hlp,close,last,closingBell,closeToLastPct,atp,hlm,ohlc," +
                    "open,high,low,close,dayTrend,last,ohlc,ocMid,oTOc," +
                    "RelativeStrength" +
                    ",AtpDelUpTrend,AtpDelDnTrend,trdLakh,delLakh,atp,atpChgPct,mvwap,fuOiLots,fuOiChgPct,buildup,AtpOiTrendI1,AtpOiTrendI2" +
                    ",atpMclose, atpMclose_pct,delDiversion" +
                    ",hlMid,atpMhlm,hld,hlp,cToL_Pct," +

//            "calcAtpFixGrowth,calcAtpDynGrowth,atpChgPct," +
//            "calcVolFixGrowth,calcVolDynGrowth,volChgPct," +
//            "delTally,calcDelFixGrowth,calcDelDynGrowth,delChgPct," +
//            "calcFoiFixGrowth,calcFoiDynGrowth,foiChgPct," +
                    "calcAtpFixGrowth,calcAtpFixGrowthChg," +
                    "calcTrdFixSpike,trdChgPct," +
                    "trdDelRatio," +
                    "sumDelGrowth,sumDelDiff," +
                    "calcDelFixSpike,delChgPct," +
//                    "calcFoiFixGrowth,foiChgPct," +

//                    "premium," +
                    "nxtCloseToOpenPct,nxtOptoHighPct,nxtOptoLowPct,nxtOptoAtpPct" +
//                    ",VolAtpMfi,DelAtpMfi,DelAtpMfiChg" +
//                    ",DelRsi,DelRsiChg,AtpRsi,AtpRsiChg" +
                    ",lotSize" +
                    ",vol_InLakh,trd_InLakh,del_InLakh,vdr" +
                    ",fuAtp,fuAtpMcmAtp_pct" +
                    ",Hammer,NR4,NR7,OeL,OeH,CeH,CeL";

    static final String DAILY_MARKET_SCAN_CSV_HEADER_1 = ""
            + "symbol,tradeDate,tradeDay,"
            + "open,high,low,close,bullDay,bearDay,"
            + "atp,atpChgPct,mvwap,fuOiLots,fuOiChgPct,fuLong,fuShort,"
            + "delDiversion,"
            + "cAtpFixGrw,cAtpFixGrwChg,"
            + "cDelFixSpike,delChgPct,"
            + "sumDelGrw,sumDelDiff,"
            + "lotSize,"
            + "fuAtp,fuAtpMcmAtp_pct,"
            + "Hammer,NR4,NR7";

    static final List<String> SHUVI_TICKERS =
            List.of("ASIANPAINTS", "COLPAL", "DABUR", "ICICIPRULI", "JUBLIENTFOOD", "PIDILITE", "RELIANCE", "SBIN", "TITAN", "UBL");
    static final List<String> PRA_TICKERS =
            List.of("BAJAJFINSRV", "HDFCBANK", "HUL", "TITAN", "LT");

}
