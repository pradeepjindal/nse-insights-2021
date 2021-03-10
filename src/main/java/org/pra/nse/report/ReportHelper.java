package org.pra.nse.report;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.db.model.*;
import org.pra.nse.util.NumberUtils;
import org.pra.nse.util.Du;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class ReportHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHelper.class);

    public static void enrichGrowth(Map<String, CalcAvgTab> calcAvgMap, Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichGrowth - ");
        LOGGER.info("enrichGrowth - total symbols: {}", symbolMap.size());
        Map.Entry<String, LocalDate> previousDate = new AbstractMap.SimpleEntry<>("tradeDate", null);
        Map.Entry<String, BigDecimal> sumDelivery = new AbstractMap.SimpleEntry<>("sumDelivery", BigDecimal.ZERO);
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            String symbol = entry.getKey();
            int fromIndex = 0;
            LocalDate dataFromDate = entry.getValue().get(fromIndex).getTradeDate();
            int toIndex = entry.getValue().size() - 1;
            LocalDate dataToDate = entry.getValue().get(toIndex).getTradeDate();
            LOGGER.info("enrichGrowth - {}, rows:{}, fromDt:{}, toDt:{}", Du.symbol(symbol), entry.getValue().size(), dataFromDate, dataToDate);
            previousDate.setValue(null);
            //DeliverySpikeDto firstDto = entry.getValue().get(0);
            CalcAvgTab calcAvgTabRow = calcAvgMap.get(symbol);
            if(calcAvgTabRow == null) {
                LOGGER.error("enrichGrowth - {}, skipping, calcAvg not found (probably new entry in FnO)", Du.symbol(symbol));
                continue;
            }
            BigDecimal atpFixOnePercent = NumberUtils.onePercent(calcAvgTabRow.getAtpSma());
            BigDecimal volFixOnePercent = NumberUtils.onePercent(calcAvgTabRow.getVolSma());
            BigDecimal delFixOnePercent = NumberUtils.onePercent(calcAvgTabRow.getDelSma());
            BigDecimal foiFixOnePercent = NumberUtils.onePercent(calcAvgTabRow.getFoiSma());
            //
            sumDelivery.setValue(BigDecimal.ZERO);
            //TODO shd be period of report or ?
            BigDecimal totalExpectedDeliveryForDuration = calcAvgTabRow.getDelSma().multiply(new BigDecimal(entry.getValue().size()));
            BigDecimal onePercentOfExpectedDelivery = NumberUtils.onePercent(totalExpectedDeliveryForDuration);

            List<DeliverySpikeDto> sortedList = entry.getValue();
            for(DeliverySpikeDto dto:entry.getValue()) {
//                LOGGER.info("enrichGrowth - looping symbol:{} for date: {} ", dto.getSymbol(), dto.getTradeDate());
                if(!symbol.equals(dto.getSymbol())) {
                    LOGGER.error("enrichGrowth - symbol mismatch");
                    throw new RuntimeException("enrichGrowth - symbol mismatch");
                }
                //CalcAvgTab tab = calcAvgMap.get(dto.getSymbol();
                BigDecimal atpDynOnePercent = NumberUtils.onePercent(calcAvgTabRow.getAtpSma());
                BigDecimal volDynOnePercent = NumberUtils.onePercent(calcAvgTabRow.getVolSma());
                BigDecimal delDynOnePercent = NumberUtils.onePercent(calcAvgTabRow.getDelSma());
                BigDecimal foiDynOnePercent = NumberUtils.onePercent(calcAvgTabRow.getFoiSma());
                //
                sumDelivery.setValue(sumDelivery.getValue().add(dto.getDelivery()));
                //TODO refactor it
                if(previousDate.getValue() == null || previousDate.getValue().isBefore(dto.getTradeDate())) {
//                    LOGGER.info("enrichGrowth - for symbol:{}, previousDate:{}, currentDate:{}", dto.getSymbol(), previousDate.getValue(), dto.getTradeDate());
                    // sum
                    previousDate.setValue(dto.getTradeDate());
                    dto.setDelAccumulation(NumberUtils.divide(sumDelivery.getValue(), onePercentOfExpectedDelivery));
                    if(dto.getBackDto().getDelAccumulation() != null) {
                        dto.setDelDiff(dto.getDelAccumulation().subtract(dto.getBackDto().getDelAccumulation()));
                    }
                    // fix
                    BigDecimal atpFixGrowth = NumberUtils.divide(dto.getAtp(), atpFixOnePercent);
                    dto.setAtpFixGrowth(atpFixGrowth);
                    BigDecimal volFixGrowth = NumberUtils.divide(dto.getVolume(), volFixOnePercent);
                    dto.setVolFixGrowth(volFixGrowth);
                    BigDecimal delFixGrowth = NumberUtils.divide(dto.getDelivery(), delFixOnePercent);
                    dto.setDelFixGrowth(delFixGrowth);
//                    BigDecimal foiFixGrowth = NumberUtils.divide(dto.getOi(), foiFixOnePercent);
//                    dto.setFoiFixGrowth(foiFixGrowth);
                    // dyn
                    BigDecimal atpDynGrowth = NumberUtils.divide(dto.getAtp(), atpDynOnePercent);
                    dto.setAtpDynGrowth(atpDynGrowth);
                    BigDecimal volDynGrowth = NumberUtils.divide(dto.getVolume(), volDynOnePercent);
                    dto.setVolDynGrowth(volDynGrowth);
                    BigDecimal delDynGrowth = NumberUtils.divide(dto.getDelivery(), delDynOnePercent);
                    dto.setDelDynGrowth(delDynGrowth);
//                    BigDecimal foiDynGrowth = NumberUtils.divide(dto.getOi(), foiDynOnePercent);
//                    dto.setFoiDynGrowth(foiDynGrowth);
                } else {
                    LOGGER.error("enrichGrowth - Date Flow MISMATCH for symbol:{}, previousDate:{}, currentDate:{}", dto.getSymbol(), previousDate.getValue(), dto.getTradeDate());
                    throw new RuntimeException("enrichGrowth - Date Flow MISMATCH");
                }
            }
        }
        LOGGER.info("enrichGrowth - completed");
    }

    public static void enrichAtpDelAndOiTrend(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichTrend - ");
        LOGGER.info("enrichTrend - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            LOGGER.info("enrichTrend - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            enrichTrendForEachSymbol(entry.getValue());
        }
        LOGGER.info("enrichTrend - completed");
    }

    private static void enrichTrendForEachSymbol(List<DeliverySpikeDto> DeliverySpikeDtoList) {
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
                     if (atpPositivePrcnt && delPositivePrcnt) dto.setAtpDelForDnTrend("Long- buyer-charging-up (high-demand)");
                else if (atpNegativePrcnt && delPositivePrcnt) dto.setAtpDelForDnTrend("Shrt- saler-pressing-dn (high-supply)");
                else if (atpPositivePrcnt && delNegativePrcnt) dto.setAtpDelForDnTrend("- fever-buyer  (low-demand)");
                else if (atpNegativePrcnt && delNegativePrcnt) dto.setAtpDelForDnTrend("- fever-saler  (low-supply)");
                else if (atpNeutralPrcnt  && delPositivePrcnt) dto.setAtpDelForDnTrend("Botm- balance-bullish (buyer incresing)");
                else if (atpNeutralPrcnt  && delNegativePrcnt) dto.setAtpDelForDnTrend("Botm- balance-bullish (seler holding)");
                else dto.setAtpDelForDnTrend("");
                //
                float oiDiff = dto.getFuOiLots().subtract(dto.getBackDto().getFuOiLots()).floatValue();
                float oiDiffPrcnt = oiDiff / NumberUtils.onePercent(dto.getBackDto().getFuOiLots()).floatValue();
                boolean oiPositive = oiDiff > 1;
                boolean oiNegative = oiDiff < -1;
                boolean oiPositivePrcnt = oiDiffPrcnt > 1;
                boolean oiNegativePrcnt = oiDiffPrcnt < -1;
                     if (atpPositiveAbs && oiPositivePrcnt) dto.setAtpOiTrend("retailCust(B)-aggressive-buy  (long-buildup)");
                else if (atpPositiveAbs && oiNegativePrcnt) dto.setAtpOiTrend("sb/SC- shrt.booking or shrt.covering");
                else if (atpNegativeAbs && oiPositivePrcnt) dto.setAtpOiTrend("institute(S) -aggressive-sale (short-buildup)");
                else if (atpNegativeAbs && oiNegativePrcnt) dto.setAtpOiTrend("LB/LC- long.booking or long.covering");
                else dto.setAtpOiTrend("");
            } catch(Exception ex) {
                LOGGER.error("", ex);
                String errMsg = "enrichTrend - " + Du.symbol(dto.getSymbol()) + ", ERROR";
                throw new RuntimeException(errMsg);
            }
        }
    }

//    public static void fillTheIndicatorsChg(LocalDate forDate, int forMinusDays) {
//        LOGGER.info("DataManager - fillTheIndicators");
//        LocalDate minDate = minDate(forDate, forMinusDays);
//        Predicate<DeliverySpikeDto> predicate = dto -> filterDate(dto, minDate, forDate);
//        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolMap = prepareDataByTradeDateAndSymbol(predicate);
//
//        //load old Rsi
//        //TODO fix this
//        List<CalcRsiTab> oldRsiList = calcRsiRepository.findAll();
//        ReportHelper.enrichRsi(oldRsiList, tradeDateAndSymbolMap);
//
//        //load old Mfi
//        //TODO fix this
//        List<CalcMfiTab> oldMfiList = calcMfiRepository.findAll();
//        ReportHelper.enrichMfi(oldMfiList, tradeDateAndSymbolMap);
//
//        LocalDate backDate = null;
//        DeliverySpikeDto backDto = null;
//        BigDecimal onePercent = null;
//        BigDecimal diff = null;
//        BigDecimal chg = null;
//        for(DeliverySpikeDto tdyDto:dbResults) {
//            if(tdyDto.getTradeDate().isAfter(tradeDates_Desc_LinkedList.get(20))) {
//                backDto = null;
//                backDate = backDateMap.get(tdyDto.getTradeDate());
//                if(tradeDateAndSymbolMap.containsKey(backDate))
//                    backDto = tradeDateAndSymbolMap.get(backDate).get(tdyDto.getSymbol());
//                if (backDto == null) {
//                    LOGGER.warn("{} - backDto is null for: {}", tdyDto.getSymbol(), backDate);
//                } else if (backDto.getDelAtpMfi() == null) {
//                    LOGGER.warn("{} - DelAtpMfi is null for: {}", tdyDto.getSymbol(), backDate);
//                } else if (backDto.getAtpRsi() == null) {
//                    LOGGER.warn("{} - AtpRsi is null for: {}", tdyDto.getSymbol(), backDate);
//                } else {
//                    //TODO no need to do percent calc here, it is already in 0 to 100 range
//                    //onePercent = backDto.getDelAtpMfi().divide(NumberUtils.HUNDRED, 2, RoundingMode.HALF_UP);
//                    onePercent = NumberUtils.onePercent(backDto.getDelAtpMfi());
//                    //diff = dto.getDelAtpMfi().divide(onePercent, 2, RoundingMode.HALF_UP);
//                    diff = NumberUtils.divide(tdyDto.getDelAtpMfi(), onePercent);
//                    chg = diff.subtract(NumberUtils.HUNDRED);
//                    tdyDto.setDelAtpMfiChg(chg);
//
//                    //onePercent = backDto.getAtpRsi().divide(NumberUtils.HUNDRED, 2, RoundingMode.HALF_UP);
//                    onePercent = NumberUtils.onePercent(backDto.getAtpRsi());
//                    //diff = dto.getAtpRsi().divide(onePercent, 2, RoundingMode.HALF_UP);
//                    diff = NumberUtils.divide(tdyDto.getAtpRsi(), onePercent);
//                    chg = diff.subtract(NumberUtils.HUNDRED);
//                    tdyDto.setAtpRsiChg(chg);
//                }
//            }
//        }
//    }

    private void calculateBellsMethodOne(DeliverySpikeDto dto) {
        String signal;
        BigDecimal hundred = new BigDecimal(100);
        BigDecimal minPlusThreshHold = new BigDecimal(0.20f);
        BigDecimal minMinusThreshHold = new BigDecimal(-0.20f);

        if(dto.getLast().compareTo(dto.getClose()) == 1) {
            BigDecimal percent = dto.getClose().divide(hundred, 2, RoundingMode.HALF_UP);
            BigDecimal diff = dto.getLast().subtract(dto.getClose());
            dto.setCloseToLastPercent(diff.divide(percent, 2, RoundingMode.HALF_UP));
            signal = dto.getCloseToLastPercent().compareTo(minPlusThreshHold) > 0 ? "bullish" : "";
            dto.setClosingBell(signal);
        } else {
            BigDecimal percent = dto.getLast().divide(hundred, 2, RoundingMode.HALF_UP);
            BigDecimal diff = dto.getClose().subtract(dto.getLast()).multiply(new BigDecimal(-1));
            dto.setCloseToLastPercent(diff.divide(percent, 2, RoundingMode.HALF_UP));
            signal = dto.getCloseToLastPercent().compareTo(minMinusThreshHold) < 0 ? "bearish" : "";
            dto.setClosingBell(signal);
        }

        if(dto.getOpen().compareTo(dto.getPreviousClose()) == 1) {
            signal = dto.getCloseToOpenPercent().compareTo(minPlusThreshHold) > 0 ? "gapUp" : "";
            dto.setOpeningBell(signal);
        } else {
            signal = dto.getCloseToOpenPercent().compareTo(minMinusThreshHold) < 0 ? "gapDown" : "";
            dto.setOpeningBell(signal);
        }
    }

    private void calculateBellsMethodTwo(DeliverySpikeDto dto) {
        String signal;
        BigDecimal hundred = new BigDecimal(100);

        BigDecimal minPlusThreshHold = new BigDecimal(0.25f);
        BigDecimal plusThreshHold = new BigDecimal(0.55f);
        BigDecimal veryPlusThreshHold = new BigDecimal(0.95f);

        BigDecimal minMinusThreshHold = new BigDecimal(-0.25f);
        BigDecimal minusThreshHold = new BigDecimal(-0.55f);
        BigDecimal veryMinusThreshHold = new BigDecimal(-0.95f);

        if(dto.getLast().compareTo(dto.getClose()) == 1) {
            BigDecimal percent = dto.getClose().divide(hundred, 2, RoundingMode.HALF_UP);
            BigDecimal diff = dto.getLast().subtract(dto.getClose());
            dto.setCloseToLastPercent(diff.divide(percent, 2, RoundingMode.HALF_UP));
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(0.20f)) > 0 ? "bullish" : "";
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(0.45f)) > 0 ? "Bullish" : signal;
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(0.75f)) > 0 ? "BULLISH" : signal;
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(0.95f)) > 0 ? "BULLISHhh" : signal;
            dto.setClosingBell(signal);
        } else {
            BigDecimal percent = dto.getLast().divide(hundred, 2, RoundingMode.HALF_UP);
            BigDecimal diff = dto.getClose().subtract(dto.getLast()).multiply(new BigDecimal(-1));
            dto.setCloseToLastPercent(diff.divide(percent, 2, RoundingMode.HALF_UP));
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(-0.20f)) < 0 ? "bearish" : "";
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(-0.45f)) < 0 ? "Bearish" : signal;
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(-0.75f)) < 0 ? "BEARISH" : signal;
            signal = dto.getCloseToLastPercent().compareTo(new BigDecimal(-0.95f)) < 0 ? "BEARISHhh" : signal;
            dto.setClosingBell(signal);
        }

        if(dto.getOpen().compareTo(dto.getPreviousClose()) == 1) {
            signal = dto.getCloseToOpenPercent().compareTo(minPlusThreshHold) > 0 ? "gapup" : "";
            signal = dto.getCloseToOpenPercent().compareTo(plusThreshHold) > 0 ? "GapUp" : signal;
            signal = dto.getCloseToOpenPercent().compareTo(veryPlusThreshHold) > 0 ? "GAPUP" : signal;
            dto.setOpeningBell(signal);
        } else {
            signal = dto.getCloseToOpenPercent().compareTo(minMinusThreshHold) < 0 ? "gapdown" : "";
            signal = dto.getCloseToOpenPercent().compareTo(minusThreshHold) < 0 ? "GapDown" : signal;
            signal = dto.getCloseToOpenPercent().compareTo(veryMinusThreshHold) < 0 ? "GAPDOWN" : signal;
            dto.setOpeningBell(signal);
        }
    }

}
