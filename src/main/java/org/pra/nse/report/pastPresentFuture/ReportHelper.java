package org.pra.nse.report.pastPresentFuture;

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
            BigDecimal trdFixOnePercent = NumberUtils.onePercent(calcAvgTabRow.getTrdSma());
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
                BigDecimal trdDynOnePercent = NumberUtils.onePercent(calcAvgTabRow.getTrdSma());
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
                    if(dto.getBackDto() != null && dto.getBackDto().getDelAccumulation() != null) {
                        dto.setDelDiff(dto.getDelAccumulation().subtract(dto.getBackDto().getDelAccumulation()));
                    }
                    // fix
                    BigDecimal atpFixGrowth = NumberUtils.divide(dto.getAtp(), atpFixOnePercent);
                    dto.setAtpFixGrowth(atpFixGrowth);
                    {
                        if(dto.getBackDto() != null && dto.getBackDto().getAtpFixGrowth() != null) {
                            BigDecimal yesAtpFixGrowth = dto.getBackDto().getAtpFixGrowth() == null ? BigDecimal.ZERO: dto.getBackDto().getAtpFixGrowth();
                            BigDecimal atpFixGrowthChg = atpFixGrowth.subtract(yesAtpFixGrowth);
                            dto.setAtpFixGrowthChg(atpFixGrowthChg);
                        }
                    }
                    BigDecimal trdFixGrowth = NumberUtils.divide(dto.getTraded(), trdFixOnePercent);
                    dto.setTrdFixGrowth(trdFixGrowth);
                    BigDecimal delFixGrowth = NumberUtils.divide(dto.getDelivery(), delFixOnePercent);
                    dto.setDelFixGrowth(delFixGrowth);
//                    BigDecimal foiFixGrowth = NumberUtils.divide(dto.getOi(), foiFixOnePercent);
//                    dto.setFoiFixGrowth(foiFixGrowth);
                    // dyn
                    BigDecimal atpDynGrowth = NumberUtils.divide(dto.getAtp(), atpDynOnePercent);
                    dto.setAtpDynGrowth(atpDynGrowth);
                    BigDecimal trdDynGrowth = NumberUtils.divide(dto.getVolume(), trdDynOnePercent);
                    dto.setTrdDynGrowth(trdDynGrowth);
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

    public static void enrichDayTrend(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichDayTrend - ");
        LOGGER.info("enrichDayTrend - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichDayTrend - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichDayTrendForEachSymbol(entry.getKey(), entry.getValue());
        }
        LOGGER.info("enrichDayTrend - completed");
    }

    public static void enrichAtpDelAndOiTrend(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichTrend - ");
        LOGGER.info("enrichTrend - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichTrend - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichTrendForEachSymbol(entry.getKey(), entry.getValue());
        }
        LOGGER.info("enrichTrend - completed");
    }

    public static void enrichNarrowRange(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichNarrowRange - ");
        LOGGER.info("enrichNarrowRange - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichNarrowRange - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichNarrowRangeForEachSymbol(entry.getValue());
        }
        LOGGER.info("enrichNarrowRange - completed");
    }

    public static void enrichHammer(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichHammer - ");
        LOGGER.info("enrichHammer - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichHammer - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichHammerForEachSymbol(entry.getValue());
        }
        LOGGER.info("enrichHammer - completed");
    }

    public static void enrichVwap(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichVwap - ");
        LOGGER.info("enrichVwap - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichVwap - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichVwapForEachSymbol(entry.getValue());
        }
        LOGGER.info("enrichVwap - completed");
    }


    public static void enrichDeliveryDiversion(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichDeliveryDiversion - ");
        LOGGER.info("enrichDeliveryDiversion - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichDeliveryDiversion - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichDeliveryDiversionForEachSymbol(entry.getValue());
        }
        LOGGER.info("enrichDeliveryDiversion - completed");
    }

    public static void enrichLongShortBuildup(Map<String, List<DeliverySpikeDto>> symbolMap) {
        LOGGER.info("enrichLongShortBuildup - ");
        LOGGER.info("enrichLongShortBuildup - total symbols: {}", symbolMap.size());
        for(Map.Entry<String, List<DeliverySpikeDto>> entry : symbolMap.entrySet()) {
            //LOGGER.info("enrichLongShortBuildup - {}, rows: {}", Du.symbol(entry.getKey()), entry.getValue().size());
            EnrichmentHelper.enrichLongShortBuildupForEachSymbol(entry.getValue());
        }
        LOGGER.info("enrichLongShortBuildup - completed");
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
