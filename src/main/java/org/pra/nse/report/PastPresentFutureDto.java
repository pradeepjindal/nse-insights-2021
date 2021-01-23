package org.pra.nse.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PastPresentFutureDto {
    private String symbol;
    private LocalDate tradeDate;
    private LocalDate backDate;
    private LocalDate nextDate;

    private BigDecimal previousClose;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal last;
    private BigDecimal atp;
    private BigDecimal highLowMid;
    private BigDecimal highLowPct;
    private String closingBell;
    private BigDecimal closeToLastPercent;

    private BigDecimal openChgPrcnt;
    private BigDecimal highChgPrcnt;
    private BigDecimal lowChgPrcnt;
    private BigDecimal closeChgPrcnt;
    private BigDecimal lastChgPrcnt;
    private BigDecimal atpChgPrcnt;

    private BigDecimal atpFixGrowth;
    private BigDecimal atpDynGrowth;
    private BigDecimal volFixGrowth;
    private BigDecimal volDynGrowth;
    private BigDecimal volumeChgPrcnt;
    private BigDecimal delFixGrowth;
    private BigDecimal delDynGrowth;
    private BigDecimal deliveryChgPrcnt;
    private BigDecimal foiFixGrowth;
    private BigDecimal foiDynGrowth;
    private BigDecimal oiChgPrcnt;
    private BigDecimal premium;

    private String openingBell;
    private BigDecimal closeToOpenPercent;

    private BigDecimal othighPrcnt;
    private BigDecimal otlowPrcnt;
    private BigDecimal otclosePrcnt;
    private BigDecimal otlastPrcnt;
    private BigDecimal otatpPrcnt;

    private BigDecimal tdycloseMinusYesclose;
    private BigDecimal tdylastMinusYeslast;
    private BigDecimal tdyatpMinusYesatp;

    private BigDecimal volume;
    private BigDecimal delivery;
    private BigDecimal oi;

    private BigDecimal volAtpMfi;
    private BigDecimal delAtpMfi;
    private BigDecimal delAtpMfiChg;

    private BigDecimal atpRsi;
    private BigDecimal atpRsiChg;
    private BigDecimal closeRsi;
    private BigDecimal lastRsi;

    private BigDecimal nxtCloseToOpenPercent;
    private BigDecimal nxtOptoHighPrcnt;
    private BigDecimal nxtOptoLowPrcnt;
    private BigDecimal nxtOptoAtpPrcnt;

    private BigDecimal delAccumulation;
    private BigDecimal ohlc;


}
