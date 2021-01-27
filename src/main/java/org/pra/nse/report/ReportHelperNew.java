package org.pra.nse.report;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.db.model.*;
import org.pra.nse.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class ReportHelperNew {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHelperNew.class);

    public static void enrichGrowth(Map<String, CalcAvgTabNew> calcAvgMap, Map<String, List<DeliverySpikeDto>> symbolMap) {
        Map.Entry<String, LocalDate> previousDate = new AbstractMap.SimpleEntry<>("tradeDate", null);
        Map.Entry<String, BigDecimal> sumDelivery = new AbstractMap.SimpleEntry<>("sumDelivery", BigDecimal.ZERO);

        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            previousDate.setValue(null);
            //DeliverySpikeDto firstDto = entry.getValue().get(0);
            String symbol = entry.getKey();
            CalcAvgTabNew calcAvgTabRow = calcAvgMap.get(symbol);
            if(calcAvgTabRow == null) {
                LOGGER.error("symbol not found: {} | probably new entry in FnO", symbol);
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

            entry.getValue().forEach( dto -> {
                if(!symbol.equals(dto.getSymbol())) {
                    LOGGER.error("symbol mismatch");
                    throw new RuntimeException("symbol mismatch");
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
                    LOGGER.error("enrichCalc | unknown condition - previousDate:{}, currentDate:{}", previousDate.getValue(), dto.getTradeDate());
                }
            });
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
