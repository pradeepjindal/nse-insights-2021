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
    private String dayTrend;
    private String bullDay;
    private String bearDay;
    private BigDecimal last;
    private BigDecimal ohlc;
    private BigDecimal avwap;
    private BigDecimal mvwap;
    private BigDecimal atp;

    private BigDecimal atpMinusClose;
    private BigDecimal atpMinusClosePrcnt;

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
    private BigDecimal atpFixGrowthChg;
    private BigDecimal atpDynGrowth;
    private BigDecimal trdFixGrowth;
    private BigDecimal trdDynGrowth;
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

    private String atpDelForUpTrend;
    private String atpDelForDnTrend;
    private String atpOiTrend;
    private String atpOiTrendI1;
    private String atpOiTrendI2;
    private String delDiversion;

    private BigDecimal volume;
    private BigDecimal traded;
    private BigDecimal delivery;
    private BigDecimal volumeMdelivery;
    private BigDecimal fuOi;
    private BigDecimal fuContracts;
    private BigDecimal fuTotTrdVal;
    private BigDecimal fuAtp;
    private BigDecimal fuAtpMinusCmAtpPct;
    private BigDecimal fuVol;
    private BigDecimal fuOiLots;
    private BigDecimal fuOiChgPrcnt;

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

    private String longShortBuildup;
    private String longBuildup;
    private String shortBuildup;

    private BigDecimal delDiff;
    private BigDecimal lotSize;

    private String hammer;
    private float NR4;
    private float NR7;
    private float HH;
    private float HL;
    private float LH;
    private float LL;


    public DeliverySpikeDto getDto(int num) {
        DeliverySpikeDto dto = this;
        if(num<0) {
            for(int i=0; num<i; i--) {
                dto = dto.getBackDto();
            }
        }
        if(num>0) {
            for(int i=0; i<num; i++) {
                dto = dto.getNextDto();
            }
        }
        return dto;
    }
    public DeliverySpikeDto getDtoNull(int num) {
        DeliverySpikeDto dto = this;
        try{
            if(num<0) {
                for(int i=0; num<i; i--) {
                    dto = dto.getBackDto();
                }
            }
            if(num>0) {
                for(int i=0; i<num; i++) {
                    dto = dto.getNextDto();
                }
            }
        } catch (NullPointerException npe) {
            dto = null;
        }
        return dto;
    }

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
                + dayTrend + ","

                + last + ","
//                + closingBell + ","

                //calculated fields
                + ohlc + ","
                + open.add(close).divide(new BigDecimal(2), 2, RoundingMode.HALF_EVEN) + ","
                + close.subtract(open) + ","
                + "ToDo,"

                + atpDelForUpTrend + ","
                + atpDelForDnTrend + ","
                + volume.subtract(delivery).divide(new BigDecimal(100000), 2, RoundingMode.HALF_EVEN) + ","
                + delivery.divide(new BigDecimal(100000), 2, RoundingMode.HALF_EVEN) + ","
                + atp + ","
                + atpChgPrcnt + ","
                + mvwap + ","
                + fuOiLots + ","
//                + atpOiTrend + ","
                + fuOiChgPrcnt + ","
                + longShortBuildup + ","
                + atpOiTrendI1 + ","
                + atpOiTrendI2 + ","

                + atpMinusClose + ","
                + atpMinusClosePrcnt + ","
                + delDiversion + ","

                + highLowMid + ","
                + atpMhlm + ","
                + highLowDiff + ","
                + highLowPct + ","
                + closeToLastPercent + ","

                + atpFixGrowth + ","
                + atpFixGrowthChg + ","
//                + atpDynGrowth + ","
                + trdFixGrowth + ","
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

//                + volAtpMfi + ","
//                + delAtpMfi + ","
//                + delAtpMfiChg + ","
//                + delRsi + ","
//                + delRsiChg + ","
//                + atpRsi + ","
//                + atpRsiChg + ","
//                + closeRsi + ","
//                + lastRsi + ","
                + lotSize + ","

                + volume.divide(new BigDecimal(100000), 2, RoundingMode.HALF_EVEN) + ","
                + volume.subtract(delivery).divide(new BigDecimal(100000), 2, RoundingMode.HALF_EVEN) + ","
                + delivery.divide(new BigDecimal(100000), 2, RoundingMode.HALF_EVEN) + ","
                + vdr + ","
                + fuAtp + ","
                + fuAtpMinusCmAtpPct + ","
                + hammer + ","
                + (NR4 == 0 ? "" : NR4) + ","
                + (NR7 == 0 ? "" : NR7) + ","
                + "OeH,OeL,CeH,CeL";
    }

    public String toDailyMarketScanString() {
        return  symbol + ","
                + tradeDate + ","
                + tradeDate.getDayOfWeek() + ","

                + open + ","
                + high + ","
                + low + ","
                + close + ","
                + bullDay + ","
                + bearDay + ","

                + atp + ","
                + atpChgPrcnt + ","
                + mvwap + ","
                + fuOiLots + ","
                + fuOiChgPrcnt + ","
                + longBuildup + ","
                + shortBuildup + ","

                + delDiversion + ","

                + atpFixGrowth + ","
                + atpFixGrowthChg + ","

                + delFixGrowth + ","
                + deliveryChgPrcnt + ","

                + delAccumulation + ","
                + delDiff + ","

                + lotSize + ","

                + fuAtp + ","
                + fuAtpMinusCmAtpPct + ","
                + hammer + ","
                + (NR4 == 0 ? "" : NR4) + ","
                + (NR7 == 0 ? "" : NR7);
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

    public BigDecimal getTrdFixGrowth() {
        return trdFixGrowth;
    }

    public void setTrdFixGrowth(BigDecimal trdFixGrowth) {
        this.trdFixGrowth = trdFixGrowth;
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

    public BigDecimal getTraded() {
        return traded;
    }

    public void setTraded(BigDecimal traded) {
        this.traded = traded;
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

    public BigDecimal getTrdDynGrowth() {
        return trdDynGrowth;
    }

    public void setTrdDynGrowth(BigDecimal trdDynGrowth) {
        this.trdDynGrowth = trdDynGrowth;
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

    public BigDecimal getLotSize() {
        return lotSize;
    }

    public void setLotSize(BigDecimal lotSize) {
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

    public BigDecimal getFuAtpMinusCmAtpPct() {
        return fuAtpMinusCmAtpPct;
    }

    public void setFuAtpMinusCmAtpPct(BigDecimal fuAtpMinusCmAtpPct) {
        this.fuAtpMinusCmAtpPct = fuAtpMinusCmAtpPct;
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

    public BigDecimal getVolumeMdelivery() {
        return volumeMdelivery;
    }

    public void setVolumeMdelivery(BigDecimal volumeMdelivery) {
        this.volumeMdelivery = volumeMdelivery;
    }

    public BigDecimal getFuVol() {
        return fuVol;
    }

    public void setFuVol(BigDecimal fuVol) {
        this.fuVol = fuVol;
    }

    public BigDecimal getFuOiLots() {
        return fuOiLots;
    }

    public void setFuOiLots(BigDecimal fuOiLots) {
        this.fuOiLots = fuOiLots;
    }

    public String getAtpDelForUpTrend() {
        return atpDelForUpTrend;
    }

    public void setAtpDelForUpTrend(String atpDelForUpTrend) {
        this.atpDelForUpTrend = atpDelForUpTrend;
    }

    public String getAtpDelForDnTrend() {
        return atpDelForDnTrend;
    }

    public void setAtpDelForDnTrend(String atpDelForDnTrend) {
        this.atpDelForDnTrend = atpDelForDnTrend;
    }

    public String getAtpOiTrend() {
        return atpOiTrend;
    }

    public void setAtpOiTrend(String atpOiTrend) {
        this.atpOiTrend = atpOiTrend;
    }

    public String getHammer() {
        return hammer;
    }

    public void setHammer(String hammer) {
        this.hammer = hammer;
    }

    public float getNR4() {
        return NR4;
    }

    public void setNR4(float NR4) {
        this.NR4 = NR4;
    }

    public float getNR7() {
        return NR7;
    }

    public void setNR7(float NR7) {
        this.NR7 = NR7;
    }

    public float getHH() {
        return HH;
    }

    public void setHH(float HH) {
        this.HH = HH;
    }

    public float getHL() {
        return HL;
    }

    public void setHL(float HL) {
        this.HL = HL;
    }

    public float getLH() {
        return LH;
    }

    public void setLH(float LH) {
        this.LH = LH;
    }

    public float getLL() {
        return LL;
    }

    public void setLL(float LL) {
        this.LL = LL;
    }

    public BigDecimal getAvwap() {
        return avwap;
    }

    public void setAvwap(BigDecimal avwap) {
        this.avwap = avwap;
    }

    public BigDecimal getMvwap() {
        return mvwap;
    }

    public void setMvwap(BigDecimal mvwap) {
        this.mvwap = mvwap;
    }

    public String getAtpOiTrendI1() {
        return atpOiTrendI1;
    }

    public void setAtpOiTrendI1(String atpOiTrendI1) {
        this.atpOiTrendI1 = atpOiTrendI1;
    }

    public String getAtpOiTrendI2() {
        return atpOiTrendI2;
    }

    public void setAtpOiTrendI2(String atpOiTrendI2) {
        this.atpOiTrendI2 = atpOiTrendI2;
    }

    public BigDecimal getFuOiChgPrcnt() {
        return fuOiChgPrcnt;
    }

    public void setFuOiChgPrcnt(BigDecimal fuOiChgPrcnt) {
        this.fuOiChgPrcnt = fuOiChgPrcnt;
    }

    public BigDecimal getAtpMinusClose() {
        return atpMinusClose;
    }

    public void setAtpMinusClose(BigDecimal atpMinusClose) {
        this.atpMinusClose = atpMinusClose;
    }

    public BigDecimal getAtpMinusClosePrcnt() {
        return atpMinusClosePrcnt;
    }

    public void setAtpMinusClosePrcnt(BigDecimal atpMinusClosePrcnt) {
        this.atpMinusClosePrcnt = atpMinusClosePrcnt;
    }

    public String getDelDiversion() {
        return delDiversion;
    }

    public void setDelDiversion(String delDiversion) {
        this.delDiversion = delDiversion;
    }

    public BigDecimal getAtpFixGrowthChg() {
        return atpFixGrowthChg;
    }

    public void setAtpFixGrowthChg(BigDecimal atpFixGrowthChg) {
        this.atpFixGrowthChg = atpFixGrowthChg;
    }

    public String getDayTrend() {
        return dayTrend;
    }

    public void setDayTrend(String dayTrend) {
        this.dayTrend = dayTrend;
    }

    public String getLongShortBuildup() {
        return longShortBuildup;
    }

    public void setLongShortBuildup(String longShortBuildup) {
        this.longShortBuildup = longShortBuildup;
    }

    public String getBullDay() {
        return bullDay;
    }

    public void setBullDay(String bullDay) {
        this.bullDay = bullDay;
    }

    public String getBearDay() {
        return bearDay;
    }

    public void setBearDay(String bearDay) {
        this.bearDay = bearDay;
    }


    public String getLongBuildup() {
        return longBuildup;
    }

    public void setLongBuildup(String longBuildup) {
        this.longBuildup = longBuildup;
    }

    public String getShortBuildup() {
        return shortBuildup;
    }

    public void setShortBuildup(String shortBuildup) {
        this.shortBuildup = shortBuildup;
    }
}
