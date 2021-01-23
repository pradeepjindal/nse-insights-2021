package org.pra.nse.statistics;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.refdata.RefData;
import org.pra.nse.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class StatisticsTopRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsTopRunner.class);

    public static void summarizeTdyHigh(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.40);
        BigDecimal LOWER_LIMIT = new BigDecimal(8);
        BigDecimal UPPER_LIMIT = new BigDecimal(12);
        BigDecimal MIN_RSI = new BigDecimal(50);
        BigDecimal TWO_HUNDRED = new BigDecimal(200);
        BigDecimal THRESHOLD = new BigDecimal(3);
        BigDecimal halfPct = new BigDecimal(.50);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        BigDecimal onePct = null;
        int rowCtr = 0;
        LOGGER.info("summarizeTdyHigh");
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
                onePct = NumberUtils.onePercent(dto.getOpen());
                chg = dto.getHigh().subtract(dto.getOpen());
                if(chg.compareTo(THRESHOLD.multiply(onePct)) == 1) {
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

    public static void summarizeTdyLow(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.40);
        BigDecimal LOWER_LIMIT = new BigDecimal(8);
        BigDecimal UPPER_LIMIT = new BigDecimal(12);
        BigDecimal MIN_RSI = new BigDecimal(50);
        BigDecimal TWO_HUNDRED = new BigDecimal(200);
        BigDecimal THRESHOLD = new BigDecimal(3);
        BigDecimal halfPct = new BigDecimal(.50);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        BigDecimal onePct = null;
        int rowCtr = 0;
        LOGGER.info("summarizeTdyLow");
        String symbol = null;
        long lotSize = 0L;
        for (List<DeliverySpikeDto> dtos : symbolMap.values()) {
            symbol = dtos.get(0).getSymbol();
            lotSize = RefData.getLotSize(symbol);
            if ("INDUSINDBK".equals(symbol)) {
                LOGGER.info("");
            }
            int oldRowCtr = rowCtr;
            float oldConditionMatched = conditionMatched;
            float oldProfitableTrades = profitableTrades;

            for (DeliverySpikeDto dto : dtos) {
                if (dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                onePct = NumberUtils.onePercent(dto.getOpen());
                chg = dto.getOpen().subtract(dto.getLow());
                if (chg.compareTo(THRESHOLD.multiply(onePct)) == 1) {
                    {
                        conditionMatched++;
                        if (dto.getNxtOptoLowPrcnt().compareTo(halfPct) == 1) {
                            profitableTrades++;
                        }
                    }
                }
            }
            float pct = (profitableTrades - oldProfitableTrades) / ((conditionMatched - oldConditionMatched) / 100);
            if (pct >= 90) {
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
        LOGGER.info("profitable percentage: {}", pct);

    }

}
