package org.pra.nse.report.pastPresentFuture;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.util.Du;
import org.pra.nse.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class EnrichmentHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichmentHelper.class);

    public static void enrichVwapForEachSymbol(List<DeliverySpikeDto> DeliverySpikeDtoList) {
        DeliverySpikeDto tmpDto;
        boolean notLogged;
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {
            BigDecimal mvwap = BigDecimal.ZERO;
            BigDecimal price = BigDecimal.ZERO;
            BigDecimal sumPrice = BigDecimal.ZERO;
            BigDecimal sumVolume = BigDecimal.ZERO;
            //int size = DeliverySpikeDtoList.size() > 19 ? -20 : (DeliverySpikeDtoList.size()-1) * -1;
            notLogged = true;
            for(int i=-20; i<0; i++) {
                try{
                    tmpDto = dto.getDto(i);
                    price = tmpDto.getAtp().multiply(tmpDto.getDelivery());
                    sumPrice = sumPrice.add(price);
                    sumVolume = sumVolume.add(tmpDto.getDelivery());
                } catch (NullPointerException npe) {
                    if(notLogged) {
                        LOGGER.warn("enrichVwap - {}, skipping, no back data (seems new entry in FnO), {}", Du.symbol(dto.getSymbol()), dto.getTradeDate());
                        notLogged = false;
                    }
                }
            }
            mvwap = NumberUtils.divide(sumPrice, sumVolume);
            dto.setMvwap(mvwap);
        }
    }

    public static void enrichHammerForEachSymbol(List<DeliverySpikeDto> DeliverySpikeDtoList) {
        String hammer;
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {
            //LOGGER.info("symbol: {}, dt: {}", dto.getSymbol(), dto.getTradeDate());
            try {
                hammer = IndicatorUtils.calculateHammerManishVersion(
                        dto.getOpen().floatValue(),
                        dto.getHigh().floatValue(),
                        dto.getLow().floatValue(),
                        dto.getClose().floatValue());
                dto.setHammer(hammer);
            } catch (NullPointerException npe) {
                LOGGER.warn("enrichHammer - {}, skipping, no back data (seems new entry in FnO), {}", Du.symbol(dto.getSymbol()), dto.getTradeDate());
            }
        }
    }

    public static void enrichNarrowRangeForEachSymbol(List<DeliverySpikeDto> DeliverySpikeDtoList) {
//        float[][] nrArray = new float[DeliverySpikeDtoList.size()+5][DeliverySpikeDtoList.size()+5];
//        int i = 0;
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {
            //LOGGER.info("symbol: {}, dt: {}", dto.getSymbol(), dto.getTradeDate());
            try {
                float t1 = dto.getHighLowDiff().floatValue();
                float t2 = dto.getBackDto().getHighLowDiff().floatValue();
                float t3 = dto.getBackDto().getBackDto().getHighLowDiff().floatValue();
                float t4 = dto.getBackDto().getBackDto().getBackDto().getHighLowDiff().floatValue();
                if( t1 < t2 && t1 < t3 && t1 < t4) {
                    dto.setNR4(t1);
                }
            } catch (NullPointerException npe) {
                LOGGER.warn("enrichNarrowRange - {}, skipping, no back data (seems new entry in FnO), {}", Du.symbol(dto.getSymbol()), dto.getTradeDate());
            }
            try {
                float t1 = dto.getHighLowDiff().floatValue();
                float t2 = dto.getDto(-1).getHighLowDiff().floatValue();
                float t3 = dto.getDto(-2).getHighLowDiff().floatValue();
                float t4 = dto.getDto(-3).getHighLowDiff().floatValue();
                float t5 = dto.getDto(-4).getHighLowDiff().floatValue();
                float t6 = dto.getDto(-5).getHighLowDiff().floatValue();
                float t7 = dto.getDto(-6).getHighLowDiff().floatValue();
                if( t1 < t2 && t1 < t3 && t1 < t4 && t1 < t5 && t1 < t6 && t1 < t7) {
                    dto.setNR7(t1);
                }
            } catch (NullPointerException npe) {
                LOGGER.warn("enrichNarrowRange - {}, skipping, no back data (seems new entry in FnO), {}", Du.symbol(dto.getSymbol()), dto.getTradeDate());
            }
        }
    }

    public static void enrichDayTrendForEachSymbol(String symbol, List<DeliverySpikeDto> DeliverySpikeDtoList) {
        String bull;
        String bear;
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {
            dto.setDayTrend("");
            dto.setBullDay("");
            dto.setBearDay("");
            try {
//                double atpMinusClose = dto.getAtp().doubleValue() - dto.getClose().doubleValue();
                double atpMinusClose = dto.getAtpMinusClose().doubleValue();
                double localAtpMinusClosePct = dto.getAtpMinusClosePrcnt().doubleValue();
                double localCloseMinusAtpPct = localAtpMinusClosePct * -1;
                if(localAtpMinusClosePct > 0) {
                    if(localAtpMinusClosePct >= .90) bear = "___BEAR*";
                    else if(localAtpMinusClosePct >= .50) bear = "___BEAR";
                    else if(localAtpMinusClosePct <= .10) bear = "_bear";
                    else bear = "__Bear";
                    dto.setDayTrend(bear);
                    dto.setBearDay(bear);
                }
                if(localAtpMinusClosePct < 0) {
                    if(localCloseMinusAtpPct >= .90) bull = "_________BULL*";
                    else if(localCloseMinusAtpPct >= .50) bull = "_________BULL";
                    else if(localCloseMinusAtpPct <= .10) bull = "_______bull";
                    else bull = "________Bull";
                    dto.setDayTrend(bull);
                    dto.setBullDay(bull.replace("______",""));
                }
            } catch(Exception ex) {
                LOGGER.error("", ex);
                String errMsg = "enrichDayTrend - " + Du.symbol(dto.getSymbol()) + ", ERROR";
                throw new RuntimeException(errMsg);
            }
        }
    }

    public static void enrichLongShortBuildupForEachSymbol(List<DeliverySpikeDto> DeliverySpikeDtoList) {
        String buildup;
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {

            float atpChg = dto.getAtpChgPrcnt().floatValue();
            float atpMinusClose = dto.getAtpMinusClosePrcnt().floatValue();
            float delChg = dto.getDeliveryChgPrcnt().floatValue();
            float fuOiChg = dto.getFuOiChgPrcnt().floatValue();

            boolean atpIsUp = atpChg > 1;
            boolean atpIsDn = atpChg < -1;

            boolean delIsUp = delChg > 50;
            boolean delIsDn = delChg < -50;

            boolean fuOiUp = fuOiChg > 4;
            boolean fuOiDn = fuOiChg < -4;

            buildup = "";
            // long build up = price is up 1%+ and fu oi is up by 4%+
            {
                boolean longBuildUp = atpIsUp && fuOiUp;

                if(longBuildUp) buildup = "LongBuildup";
                dto.setLongShortBuildup(buildup);
                dto.setLongBuildup(buildup);
            }

            buildup = "";
            // short build up = price is dn 1%+ and fu oi is dn by 4%+
            {
                boolean shortBuildUp = atpIsDn && fuOiDn;

                if(shortBuildUp) buildup = "____ShortBuildup";
                dto.setLongShortBuildup(buildup);
                dto.setShortBuildup(buildup.replace("_",""));
            }

        }
    }

    public static void enrichDeliveryDiversionForEachSymbol(List<DeliverySpikeDto> DeliverySpikeDtoList) {
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {

            float atpChg = dto.getAtpChgPrcnt().floatValue();
            float atpMinusClose = dto.getAtpMinusClosePrcnt().floatValue();
            float delChg = dto.getDeliveryChgPrcnt().floatValue();

            dto.setDelDiversion("");

            // bear day but still bear lost the money, seems del diversion for up side reversal
            {
                boolean delIsUp = delChg > 0;
                boolean atpIsDn = atpChg < 0;
                boolean bearLostMoney = atpMinusClose < 0;
                boolean bullLostMoney = atpMinusClose > 0;
                if(delIsUp && atpIsDn && bearLostMoney) {
                    dto.setDelDiversion(dto.getDelDiversion() + "BearDay-Bear-LostMoney");
                }
            }

            // bull day but still bull lost the money, seems del diversion for dn side reversal
            {
                boolean delIsUp = delChg > 0;
                boolean atpIsUp = atpChg > 0;
                boolean bearLostMoney = atpMinusClose < 0;
                boolean bullLostMoney = atpMinusClose > 0;
                if(delIsUp && atpIsUp && bullLostMoney) {
                    dto.setDelDiversion(dto.getDelDiversion() + "_BullDay-Bull-LostMoney");
                }
            }

        }
    }

    public static void enrichTrendForEachSymbol(String symbol, List<DeliverySpikeDto> DeliverySpikeDtoList) {
        int i = 0;
        for(DeliverySpikeDto dto: DeliverySpikeDtoList) {
            try {
                if(dto.getBackDto() == null) {
                    LOGGER.info("enrichTrend - {}, skipping, no back data (seems new entry in FnO)", Du.symbol(dto.getSymbol()));
                    break;
                } else {
                    //enable for debugging logs
//                    LOGGER.info("enrichTrend - {}, trdDt: {}, oi: {}", Du.symbol(dto.getSymbol()), dto.getTradeDate(), dto.getFuOi());
                }
                float fuOi = dto.getFuOi().floatValue();
                if(fuOi == 0) {
                    LOGGER.warn("enrichTrend - {}, oi: {}, fuTrdVal: {}", Du.symbol(dto.getSymbol()), dto.getFuOi(), dto.getFuTotTrdVal());
                }
                float atpDiffAbs = dto.getAtp().subtract(dto.getBackDto().getAtp()).floatValue();
                float atpDiffPrcnt = atpDiffAbs / NumberUtils.onePercent(dto.getBackDto().getAtp()).intValue();
                boolean atpPositiveAbs = atpDiffAbs > 1;
                boolean atpNegativeAbs = atpDiffAbs < -1;
                boolean atpNeutralAbs = !atpPositiveAbs && !atpNegativeAbs;
                boolean atpPositivePrcnt = atpDiffPrcnt > 1;
                boolean atpNegativePrcnt = atpDiffPrcnt < -1;
                boolean atpNeutralPrcnt = !atpPositivePrcnt && !atpNegativePrcnt;
                //
                BigDecimal halfPercent = new BigDecimal(200);
                float closeMinusAtpAbs = dto.getClose().subtract(dto.getAtp()).floatValue();
                float closeMinusAtpPrcnt = closeMinusAtpAbs / NumberUtils.percent(dto.getAtp(), halfPercent).intValue();
                boolean cMaPositiveAbs = closeMinusAtpAbs > 1;
                boolean cMaNegativeAbs = closeMinusAtpAbs < -1;
                boolean cMaNeutralAbs = !cMaPositiveAbs && !cMaNegativeAbs;
                boolean cMaPositivePrcnt = closeMinusAtpPrcnt > 1;
                boolean cMaNegativePrcnt = closeMinusAtpPrcnt < -1;
                boolean cMaNeutralPrcnt = !cMaPositivePrcnt && !cMaNegativePrcnt;
                //
                float delDiffAbs = dto.getDelivery().subtract(dto.getBackDto().getDelivery()).floatValue();
                float delDiffPrcnt = delDiffAbs / NumberUtils.onePercent(dto.getBackDto().getDelivery()).intValue();
                boolean delPositiveAbs = delDiffAbs > 1;
                boolean delNegativeAbs = delDiffAbs < -1;
                boolean delNeutral = !delPositiveAbs && !delNegativeAbs;
                boolean delPositivePrcnt = delDiffPrcnt > 1;
                boolean delNegativePrcnt = delDiffPrcnt < -1;
                boolean delNeutralPrcnt = !delPositivePrcnt && !delNegativePrcnt;

//                     if (atpPositive && delPositive) dto.setAtpDelTrend("buyer-charging-up (demand-high)");
//                else if (atpPositive && delNegative) dto.setAtpDelTrend("buyer-hesitating  (demand-hiccup)");
//                else if (atpNegative && delPositive) dto.setAtpDelTrend("saler-flooding-dn (supply-high)");
//                else if (atpNegative && delNegative) dto.setAtpDelTrend("saler-hesitating  (supply-hiccup)");
//                else dto.setAtpDelTrend("");
                if (atpPositiveAbs && delPositiveAbs) dto.setAtpDelForUpTrend("Long- buyer-charging-up (high-demand)");
                else if (atpNegativeAbs && delPositiveAbs) dto.setAtpDelForUpTrend("Shrt- saler-pressing-dn (high-supply)");
                    // to buy in delivery - supply can not be low, as intraday trader provides unlimited supply on higher price
                    // but if delivery is
                    // to sel in delivery - demand can not be low, as intraday trader provides unlimited demand on lower price
                else if (atpPositiveAbs && delNegativeAbs) dto.setAtpDelForUpTrend("- fever-buyer  (low-demand)");
                else if (atpNegativeAbs && delNegativeAbs) dto.setAtpDelForUpTrend("- fever-saler  (low-supply)");
                else if (atpNeutralAbs  && delPositiveAbs) dto.setAtpDelForUpTrend("Peek- balance-bearish (seler incresing)");
                else if (atpNeutralAbs  && delNegativeAbs) dto.setAtpDelForUpTrend("Peek- balance-bearish (buyer holding)");
                else dto.setAtpDelForUpTrend("");

                //
                if (atpPositivePrcnt && delPositivePrcnt) dto.setAtpDelForDnTrend("Long- more-buyer (high-demand)");
                else if (atpNegativePrcnt && delPositivePrcnt) dto.setAtpDelForDnTrend("Shrt- more-saler (high-supply)");
                else if (atpPositivePrcnt && delNegativePrcnt) dto.setAtpDelForDnTrend("bear- less-buyer  (low-demand)");
                else if (atpNegativePrcnt && delNegativePrcnt) dto.setAtpDelForDnTrend("bull- less-saler  (low-supply)");
                else if (atpNeutralPrcnt  && delPositivePrcnt) dto.setAtpDelForDnTrend("bear- flat-price-balance (supply-surplus)");
                else if (atpNeutralPrcnt  && delNegativePrcnt) dto.setAtpDelForDnTrend("bull- flat-stock-balance (supply-drying)");
                else dto.setAtpDelForDnTrend("");
                //
                float oiDiff = dto.getFuOiLots().subtract(dto.getBackDto().getFuOiLots()).floatValue();
                float oiDiffPrcnt = oiDiff / NumberUtils.onePercent(dto.getBackDto().getFuOiLots()).floatValue();
                boolean oiPositive = oiDiff > 1;
                boolean oiNegative = oiDiff < -1;
                boolean oiPositivePrcnt = oiDiffPrcnt > 1;
                boolean oiNegativePrcnt = oiDiffPrcnt < -1;
                if (atpPositiveAbs && oiPositivePrcnt) dto.setAtpOiTrendI1("retailCust(B)-aggressive-buy  (long-buildup)");
                else if (atpPositiveAbs && oiNegativePrcnt) dto.setAtpOiTrendI1("retailCust(B)-hesitating (profit-booking)-i-loss-booking");
                else if (atpNegativeAbs && oiPositivePrcnt) dto.setAtpOiTrendI1("institute(S) -aggressive-sale (short-buildup)");
                else if (atpNegativeAbs && oiNegativePrcnt) dto.setAtpOiTrendI1("institute(S) -hesitating (profit-booking)-r-loss-booking");
                else dto.setAtpOiTrendI1("");
                if (atpPositiveAbs && oiPositivePrcnt) dto.setAtpOiTrendI2("retailCust(B)-aggressive-buy  (long-buildup)");
                else if (atpPositiveAbs && oiNegativePrcnt) dto.setAtpOiTrendI2("SB/SC- shrt.booking or shrt.covering");
                else if (atpNegativeAbs && oiPositivePrcnt) dto.setAtpOiTrendI2("institute(S) -aggressive-sale (short-buildup)");
                else if (atpNegativeAbs && oiNegativePrcnt) dto.setAtpOiTrendI2("LB/LC- long.booking or long.covering");
                else dto.setAtpOiTrendI2("");
            } catch(Exception ex) {
                LOGGER.error("", ex);
                String errMsg = "enrichTrend - " + Du.symbol(dto.getSymbol()) + ", ERROR";
                throw new RuntimeException(errMsg);
            }
        }
    }

}
