package org.pra.nse.statistics;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.refdata.RefData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class StatisticsOpenIsHigh {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsOpenIsHigh.class);

    public static void summarizeOpenIsHigh(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal LOWER_LIMIT = new BigDecimal(8);
        BigDecimal UPPER_LIMIT = new BigDecimal(12);
        BigDecimal MIN_RSI = new BigDecimal(50);
        BigDecimal TWO_HUNDRED = new BigDecimal(200);
        BigDecimal halfPct = new BigDecimal(.50);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizeOpenIsHigh");
        String symbol = null;
        long lotSize = 0L;
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {

            symbol = dtos.get(0).getSymbol();
            lotSize = RefData.getLotSize(symbol);
            if("INDUSINDBK".equals(symbol)) {
                LOGGER.info("");
            }
            int oldRowCtr = rowCtr;
            float oldConditionMatched = conditionMatched;
            float oldProfitableTrades = profitableTrades;

            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                if(dto.getOpen().compareTo(dto.getHigh()) == 0) {
                    conditionMatched++;
                    if(dto.getNxtOptoHighPrcnt().compareTo(halfPct) == 1) {
                        profitableTrades++;
                    }
                }
            }
            float pct = (profitableTrades-oldProfitableTrades) / ((conditionMatched-oldConditionMatched) / 100);
            if(pct >= 90) {
                LOGGER.info("");
                LOGGER.info("symbol:{}, lotsize: {}", symbol, lotSize);
                LOGGER.info("total rows: {}", rowCtr - oldRowCtr);
                LOGGER.info("condition matched: {}", conditionMatched - oldConditionMatched);
                LOGGER.info("profitable trades: {}", profitableTrades - oldProfitableTrades);
                LOGGER.info("profitable percentage: {}", pct);
            }
        }
        LOGGER.info("========== ==========");
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

    public static void summarizeOpenIsLow(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal LOWER_LIMIT = new BigDecimal(8);
        BigDecimal UPPER_LIMIT = new BigDecimal(12);
        BigDecimal MIN_RSI = new BigDecimal(50);
        BigDecimal TWO_HUNDRED = new BigDecimal(200);
        BigDecimal halfPct = new BigDecimal(.50);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizeOpenIsLow");
        String symbol = null;
        long lotSize = 0L;
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {

            symbol = dtos.get(0).getSymbol();
            lotSize = RefData.getLotSize(symbol);
            if("INDUSINDBK".equals(symbol)) {
                LOGGER.info("");
            }
            int oldRowCtr = rowCtr;
            float oldConditionMatched = conditionMatched;
            float oldProfitableTrades = profitableTrades;

            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                if(dto.getOpen().compareTo(dto.getLow()) == 0) {
                    conditionMatched++;
                    if(dto.getNxtOptoLowPrcnt().compareTo(halfPct) == 1) {
                        profitableTrades++;
                    }
                }
            }
            float pct = (profitableTrades-oldProfitableTrades) / ((conditionMatched-oldConditionMatched) / 100);
            if(pct >= 90) {
                LOGGER.info("");
                LOGGER.info("symbol:{}, lotsize: {}", symbol, lotSize);
                LOGGER.info("total rows: {}", rowCtr - oldRowCtr);
                LOGGER.info("condition matched: {}", conditionMatched - oldConditionMatched);
                LOGGER.info("profitable trades: {}", profitableTrades - oldProfitableTrades);
                LOGGER.info("profitable percentage: {}", pct);
            }
        }
        LOGGER.info("========== ==========");
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

}
