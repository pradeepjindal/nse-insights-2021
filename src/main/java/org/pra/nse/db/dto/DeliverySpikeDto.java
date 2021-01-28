package org.pra.nse.db.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class DeliverySpikeDto {
    private String symbol;
    private LocalDate tradeDate;
    private LocalDate backDate;
    private LocalDate nextDate;
    private DeliverySpikeDto backDto;
    private DeliverySpikeDto nextDto;

    private BigDecimal previousClose;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal last;
    private BigDecimal ohlc;
    private BigDecimal atp;
    private BigDecimal highLowMid; //hlm
    private BigDecimal atpMhlm;
    private BigDecimal highLowDiff;
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
    private BigDecimal fuPremium;

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
    private BigDecimal tdydelMinusYesdel;

    private BigDecimal volume;
    private BigDecimal delivery;
    private BigDecimal volumeMdelivery;
    private BigDecimal fuOi;
    private BigDecimal fuContracts;
    private BigDecimal fuTotTrdVal;
    private BigDecimal fuAtp;
    private BigDecimal fuAtpMinusCmAtp;

    private BigDecimal volAtpMfi;
    private BigDecimal delAtpMfi;
    private BigDecimal delAtpMfiChg;

    private BigDecimal delRsi;
    private BigDecimal delRsiChg;

    private BigDecimal atpRsi;
    private BigDecimal atpRsiChg;
    private BigDecimal closeRsi;
    private BigDecimal lastRsi;

    private BigDecimal nxtCloseToOpenPercent;
    private BigDecimal nxtOptoHighPrcnt;
    private BigDecimal nxtOptoLowPrcnt;
    private BigDecimal nxtOptoAtpPrcnt;

    private BigDecimal vdr;
    private BigDecimal delAccumulation;



    private BigDecimal delDiff;
    private long lotSize;

    public String toCsvString() {
        return  symbol + ","
                + tradeDate + ","

                + open + ","
                + high + ","
                + low + ","
                + close + ","
                + volumeChgPrcnt + ","
                + deliveryChgPrcnt + ","
                + othighPrcnt + ","
                + otlowPrcnt;
    }

    public String toFullCsvString() {
        return  symbol + ","
                + tradeDate + ","

                + open + ","
                + high + ","
                + low + ","
                + close + ","
                + last + ","
                + atp + ","
                + highLowMid + ","

                + openChgPrcnt + ","
                + highChgPrcnt + ","
                + lowChgPrcnt + ","
                + closeChgPrcnt + ","
                + lastChgPrcnt + ","
                + atpChgPrcnt + ","

                + volumeChgPrcnt + ","
                + deliveryChgPrcnt + ","
                + oiChgPrcnt + ","
                + fuPremium + ","

                + othighPrcnt + ","
                + otlowPrcnt + ","
                + otclosePrcnt + ","
                + otlastPrcnt + ","
                + otatpPrcnt + ","

//                + tdycloseMinusYesclose + ","
//                + tdylastMinusYeslast + ","
//                + tdyatpMinusYesatp + ","

                + closeRsi + ","
                + lastRsi + ","
                + atpRsi;
    }

    public String toPpfString2() {
        return  symbol + ","
                + tradeDate + ","
                + tradeDate.getDayOfWeek() + ","

                + open + ","
                + high + ","
                + low + ","
                + close + ","
                + last + ","
//                + closingBell + ","

                //calculated fields
                + ohlc + ","
                + atp + ","
                + highLowMid + ","
                + atpMhlm + ","
                + highLowDiff + ","
                + highLowPct + ","
                + closeToLastPercent + ","

                + atpFixGrowth + ","
//                + atpDynGrowth + ","
                + atpChgPrcnt + ","
                + volFixGrowth + ","
//                + volDynGrowth + ","
                + volumeChgPrcnt + ","

                + vdr + ","
                + delAccumulation + ","
                + delDiff + ","
                + delFixGrowth + ","
//                + delDynGrowth + ","
                + deliveryChgPrcnt + ","
//                + foiFixGrowth + ","
//                + foiDynGrowth + ","
//                + oiChgPrcnt + ","
//
//                + premium + ","
//                + openingBell + ","

                + nxtCloseToOpenPercent + ","
                + nxtOptoHighPrcnt + ","
                + nxtOptoLowPrcnt + ","
                + nxtOptoAtpPrcnt + ","

                + volAtpMfi + ","
                + delAtpMfi + ","
                + delAtpMfiChg + ","

                + delRsi + ","
                + delRsiChg + ","

                + atpRsi + ","
                + atpRsiChg + ","
//                + closeRsi + ","
//                + lastRsi + ","
                + lotSize + ","
                + volume.divide(new BigDecimal(100000), 0, RoundingMode.HALF_DOWN) + ","
                + volume.subtract(delivery).divide(new BigDecimal(100000), 0, RoundingMode.HALF_DOWN) + ","
                + delivery.divide(new BigDecimal(100000), 0, RoundingMode.HALF_DOWN) + ","
                + vdr + ","
                + fuAtp + ","
                + fuAtpMinusCmAtp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHighLowPct() {
        return highLowPct;
    }

    public void setHighLowPct(BigDecimal highLowPct) {
        this.highLowPct = highLowPct;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getAtp() {
        return atp;
    }

    public void setAtp(BigDecimal atp) {
        this.atp = atp;
    }

    public BigDecimal getHighLowMid() {
        return highLowMid;
    }

    public void setHighLowMid(BigDecimal highLowMid) {
        this.highLowMid = highLowMid;
    }

    public String getClosingBell() {
        return closingBell;
    }

    public void setClosingBell(String closingBell) {
        this.closingBell = closingBell;
    }

    public BigDecimal getCloseToLastPercent() {
        return closeToLastPercent;
    }

    public void setCloseToLastPercent(BigDecimal closeToLastPercent) {
        this.closeToLastPercent = closeToLastPercent;
    }

    public BigDecimal getOpenChgPrcnt() {
        return openChgPrcnt;
    }

    public void setOpenChgPrcnt(BigDecimal openChgPrcnt) {
        this.openChgPrcnt = openChgPrcnt;
    }

    public BigDecimal getHighChgPrcnt() {
        return highChgPrcnt;
    }

    public void setHighChgPrcnt(BigDecimal highChgPrcnt) {
        this.highChgPrcnt = highChgPrcnt;
    }

    public BigDecimal getLowChgPrcnt() {
        return lowChgPrcnt;
    }

    public void setLowChgPrcnt(BigDecimal lowChgPrcnt) {
        this.lowChgPrcnt = lowChgPrcnt;
    }

    public BigDecimal getCloseChgPrcnt() {
        return closeChgPrcnt;
    }

    public void setCloseChgPrcnt(BigDecimal closeChgPrcnt) {
        this.closeChgPrcnt = closeChgPrcnt;
    }

    public BigDecimal getLastChgPrcnt() {
        return lastChgPrcnt;
    }

    public void setLastChgPrcnt(BigDecimal lastChgPrcnt) {
        this.lastChgPrcnt = lastChgPrcnt;
    }

    public BigDecimal getAtpChgPrcnt() {
        return atpChgPrcnt;
    }

    public void setAtpChgPrcnt(BigDecimal atpChgPrcnt) {
        this.atpChgPrcnt = atpChgPrcnt;
    }

    public BigDecimal getAtpFixGrowth() {
        return atpFixGrowth;
    }

    public void setAtpFixGrowth(BigDecimal atpFixGrowth) {
        this.atpFixGrowth = atpFixGrowth;
    }

    public BigDecimal getVolFixGrowth() {
        return volFixGrowth;
    }

    public void setVolFixGrowth(BigDecimal volFixGrowth) {
        this.volFixGrowth = volFixGrowth;
    }

    public BigDecimal getVolumeChgPrcnt() {
        return volumeChgPrcnt;
    }

    public void setVolumeChgPrcnt(BigDecimal volumeChgPrcnt) {
        this.volumeChgPrcnt = volumeChgPrcnt;
    }

    public BigDecimal getDelFixGrowth() {
        return delFixGrowth;
    }

    public void setDelFixGrowth(BigDecimal delFixGrowth) {
        this.delFixGrowth = delFixGrowth;
    }

    public BigDecimal getDeliveryChgPrcnt() {
        return deliveryChgPrcnt;
    }

    public void setDeliveryChgPrcnt(BigDecimal deliveryChgPrcnt) {
        this.deliveryChgPrcnt = deliveryChgPrcnt;
    }

    public BigDecimal getFoiFixGrowth() {
        return foiFixGrowth;
    }

    public void setFoiFixGrowth(BigDecimal foiFixGrowth) {
        this.foiFixGrowth = foiFixGrowth;
    }

    public BigDecimal getOiChgPrcnt() {
        return oiChgPrcnt;
    }

    public void setOiChgPrcnt(BigDecimal oiChgPrcnt) {
        this.oiChgPrcnt = oiChgPrcnt;
    }

    public BigDecimal getFuPremium() {
        return fuPremium;
    }

    public void setFuPremium(BigDecimal fuPremium) {
        this.fuPremium = fuPremium;
    }

    public String getOpeningBell() {
        return openingBell;
    }

    public void setOpeningBell(String openingBell) {
        this.openingBell = openingBell;
    }

    public BigDecimal getCloseToOpenPercent() {
        return closeToOpenPercent;
    }

    public void setCloseToOpenPercent(BigDecimal closeToOpenPercent) {
        this.closeToOpenPercent = closeToOpenPercent;
    }

    public BigDecimal getOthighPrcnt() {
        return othighPrcnt;
    }

    public void setOthighPrcnt(BigDecimal othighPrcnt) {
        this.othighPrcnt = othighPrcnt;
    }

    public BigDecimal getOtlowPrcnt() {
        return otlowPrcnt;
    }

    public void setOtlowPrcnt(BigDecimal otlowPrcnt) {
        this.otlowPrcnt = otlowPrcnt;
    }

    public BigDecimal getOtclosePrcnt() {
        return otclosePrcnt;
    }

    public void setOtclosePrcnt(BigDecimal otclosePrcnt) {
        this.otclosePrcnt = otclosePrcnt;
    }

    public BigDecimal getOtlastPrcnt() {
        return otlastPrcnt;
    }

    public void setOtlastPrcnt(BigDecimal otlastPrcnt) {
        this.otlastPrcnt = otlastPrcnt;
    }

    public BigDecimal getOtatpPrcnt() {
        return otatpPrcnt;
    }

    public void setOtatpPrcnt(BigDecimal otatpPrcnt) {
        this.otatpPrcnt = otatpPrcnt;
    }

    public BigDecimal getTdycloseMinusYesclose() {
        return tdycloseMinusYesclose;
    }

    public void setTdycloseMinusYesclose(BigDecimal tdycloseMinusYesclose) {
        this.tdycloseMinusYesclose = tdycloseMinusYesclose;
    }

    public BigDecimal getTdylastMinusYeslast() {
        return tdylastMinusYeslast;
    }

    public void setTdylastMinusYeslast(BigDecimal tdylastMinusYeslast) {
        this.tdylastMinusYeslast = tdylastMinusYeslast;
    }

    public BigDecimal getTdyatpMinusYesatp() {
        return tdyatpMinusYesatp;
    }

    public void setTdyatpMinusYesatp(BigDecimal tdyatpMinusYesatp) {
        this.tdyatpMinusYesatp = tdyatpMinusYesatp;
    }

    public BigDecimal getTdydelMinusYesdel() {
        return tdydelMinusYesdel;
    }

    public void setTdydelMinusYesdel(BigDecimal tdydelMinusYesdel) {
        this.tdydelMinusYesdel = tdydelMinusYesdel;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getDelivery() {
        return delivery;
    }

    public void setDelivery(BigDecimal delivery) {
        this.delivery = delivery;
    }

    public BigDecimal getFuOi() {
        return fuOi;
    }

    public void setFuOi(BigDecimal fuOi) {
        this.fuOi = fuOi;
    }

    public BigDecimal getVolAtpMfi() {
        return volAtpMfi;
    }

    public void setVolAtpMfi(BigDecimal volAtpMfi) {
        this.volAtpMfi = volAtpMfi;
    }

    public BigDecimal getDelAtpMfi() {
        return delAtpMfi;
    }

    public void setDelAtpMfi(BigDecimal delAtpMfi) {
        this.delAtpMfi = delAtpMfi;
    }

    public BigDecimal getDelRsi() {
        return delRsi;
    }

    public void setDelRsi(BigDecimal delRsi) {
        this.delRsi = delRsi;
    }

    public BigDecimal getDelRsiChg() {
        return delRsiChg;
    }

    public void setDelRsiChg(BigDecimal delRsiChg) {
        this.delRsiChg = delRsiChg;
    }

    public BigDecimal getCloseRsi() {
        return closeRsi;
    }

    public void setCloseRsi(BigDecimal closeRsi) {
        this.closeRsi = closeRsi;
    }

    public BigDecimal getLastRsi() {
        return lastRsi;
    }

    public void setLastRsi(BigDecimal lastRsi) {
        this.lastRsi = lastRsi;
    }

    public BigDecimal getAtpRsi() {
        return atpRsi;
    }

    public void setAtpRsi(BigDecimal atpRsi) {
        this.atpRsi = atpRsi;
    }

    public BigDecimal getNxtCloseToOpenPercent() {
        return nxtCloseToOpenPercent;
    }

    public void setNxtCloseToOpenPercent(BigDecimal nxtCloseToOpenPercent) {
        this.nxtCloseToOpenPercent = nxtCloseToOpenPercent;
    }

    public BigDecimal getNxtOptoHighPrcnt() {
        return nxtOptoHighPrcnt;
    }

    public void setNxtOptoHighPrcnt(BigDecimal nxtOptoHighPrcnt) {
        this.nxtOptoHighPrcnt = nxtOptoHighPrcnt;
    }

    public BigDecimal getNxtOptoLowPrcnt() {
        return nxtOptoLowPrcnt;
    }

    public void setNxtOptoLowPrcnt(BigDecimal nxtOptoLowPrcnt) {
        this.nxtOptoLowPrcnt = nxtOptoLowPrcnt;
    }

    public BigDecimal getNxtOptoAtpPrcnt() {
        return nxtOptoAtpPrcnt;
    }

    public void setNxtOptoAtpPrcnt(BigDecimal nxtOptoAtpPrcnt) {
        this.nxtOptoAtpPrcnt = nxtOptoAtpPrcnt;
    }

    public BigDecimal getAtpDynGrowth() {
        return atpDynGrowth;
    }

    public void setAtpDynGrowth(BigDecimal atpDynGrowth) {
        this.atpDynGrowth = atpDynGrowth;
    }

    public BigDecimal getVolDynGrowth() {
        return volDynGrowth;
    }

    public void setVolDynGrowth(BigDecimal volDynGrowth) {
        this.volDynGrowth = volDynGrowth;
    }

    public BigDecimal getDelDynGrowth() {
        return delDynGrowth;
    }

    public void setDelDynGrowth(BigDecimal delDynGrowth) {
        this.delDynGrowth = delDynGrowth;
    }

    public BigDecimal getFoiDynGrowth() {
        return foiDynGrowth;
    }

    public void setFoiDynGrowth(BigDecimal foiDynGrowth) {
        this.foiDynGrowth = foiDynGrowth;
    }

    public BigDecimal getDelAccumulation() {
        return delAccumulation;
    }

    public void setDelAccumulation(BigDecimal delAccumulation) {
        this.delAccumulation = delAccumulation;
    }

    public BigDecimal getOhlc() {
        return ohlc;
    }

    public void setOhlc(BigDecimal ohlc) {
        this.ohlc = ohlc;
    }

    public LocalDate getBackDate() {
        return backDate;
    }

    public void setBackDate(LocalDate backDate) {
        this.backDate = backDate;
    }

    public LocalDate getNextDate() {
        return nextDate;
    }

    public void setNextDate(LocalDate nextDate) {
        this.nextDate = nextDate;
    }

    public BigDecimal getDelAtpMfiChg() {
        return delAtpMfiChg;
    }

    public void setDelAtpMfiChg(BigDecimal delAtpMfiChg) {
        this.delAtpMfiChg = delAtpMfiChg;
    }

    public BigDecimal getAtpRsiChg() {
        return atpRsiChg;
    }

    public void setAtpRsiChg(BigDecimal atpRsiChg) {
        this.atpRsiChg = atpRsiChg;
    }

    public BigDecimal getHighLowDiff() {
        return highLowDiff;
    }

    public void setHighLowDiff(BigDecimal highLowDiff) {
        this.highLowDiff = highLowDiff;
    }

    public BigDecimal getVdr() {
        return vdr;
    }

    public void setVdr(BigDecimal vdr) {
        this.vdr = vdr;
    }

    public long getLotSize() {
        return lotSize;
    }

    public void setLotSize(long lotSize) {
        this.lotSize = lotSize;
    }

    public BigDecimal getFuContracts() {
        return fuContracts;
    }

    public void setFuContracts(BigDecimal fuContracts) {
        this.fuContracts = fuContracts;
    }

    public BigDecimal getFuTotTrdVal() {
        return fuTotTrdVal;
    }

    public void setFuTotTrdVal(BigDecimal fuTotTrdVal) {
        this.fuTotTrdVal = fuTotTrdVal;
    }

    public BigDecimal getFuAtp() {
        return fuAtp;
    }

    public void setFuAtp(BigDecimal fuAtp) {
        this.fuAtp = fuAtp;
    }

    public BigDecimal getFuAtpMinusCmAtp() {
        return fuAtpMinusCmAtp;
    }

    public void setFuAtpMinusCmAtp(BigDecimal fuAtpMinusCmAtp) {
        this.fuAtpMinusCmAtp = fuAtpMinusCmAtp;
    }

    public BigDecimal getAtpMhlm() {
        return atpMhlm;
    }

    public void setAtpMhlm(BigDecimal atpMhlm) {
        this.atpMhlm = atpMhlm;
    }

    public BigDecimal getDelDiff() {
        return delDiff;
    }

    public void setDelDiff(BigDecimal delDiff) {
        this.delDiff = delDiff;
    }

    public DeliverySpikeDto getBackDto() {
        return backDto;
    }

    public void setBackDto(DeliverySpikeDto backDto) {
        this.backDto = backDto;
    }

    public DeliverySpikeDto getNextDto() {
        return nextDto;
    }

    public void setNextDto(DeliverySpikeDto nextDto) {
        this.nextDto = nextDto;
    }
}
