package org.pra.nse.statistics;

import org.pra.nse.service.DataService;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class Statisian {
    private static final Logger LOGGER = LoggerFactory.getLogger(Statisian.class);

    private final DataService dataService;

    public Statisian(DataService dataService) {
        this.dataService = dataService;
    }

    public void stats(LocalDate forDate, int forMinusDays) {
        Map<String, List<DeliverySpikeDto>> symbolMap = dataService.getRichDataBySymbol(forDate, 20);
        LOGGER.info(" ........................................... ");
        LOGGER.info(" |||||||||| ----- Statisian ----- |||||||||| ");
//        StatisticsOpenHalf.summarizeHalfPercentPlus(symbolMap);
//        StatisticsOpenHalf.summarizeHalfPercentMinus(symbolMap);

//        StatisticsOpenIsHigh.summarizeOpenIsHigh(symbolMap);
//        StatisticsOpenIsHigh.summarizeOpenIsLow(symbolMap);

//        StatisticsTopRunner.summarizeTdyHigh(symbolMap);
//        StatisticsTopRunner.summarizeTdyLow(symbolMap);

        //summarizePlusDel(symbolMap);
        //summarizePlusDelAndRsi(symbolMap);
        //summarizeMinsuDel(symbolMap);
        //summarizeMinsuDelAndRsi(symbolMap);
        //summarizeRsi(symbolMap);
        //summarizeDelPlusRsi(symbolMap);
    }

    public static void summarizePlusDel(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal LOWER_LIMIT = new BigDecimal(8);
        BigDecimal UPPER_LIMIT = new BigDecimal(13);
        BigDecimal HUNDRED = new BigDecimal(100);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizePlusDel");
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {
            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                chg = dto.getDeliveryChgPrcnt();
                if(chg.compareTo(LOWER_LIMIT) == 1 && chg.compareTo(UPPER_LIMIT) == -1) {
                    conditionMatched++;
                    swing = dto.getNxtOptoHighPrcnt();
                    if(swing.compareTo(MIN_PROFIT) == 1) {
                        profitableTrades++;
                    }
                }
            }
        }
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

    public static void summarizePlusDelAndRsi(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal LOWER_LIMIT = new BigDecimal(8);
        BigDecimal UPPER_LIMIT = new BigDecimal(12);
        BigDecimal MIN_RSI = new BigDecimal(50);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizePlusDelAndRsi");
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {
            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                chg = dto.getDeliveryChgPrcnt();
                //LOGGER.info("chg: {}, rsi: {}", chg, dto.getAtpRsi());
                if(chg.compareTo(LOWER_LIMIT) == 1 && chg.compareTo(UPPER_LIMIT) == -1 && MIN_RSI.compareTo(dto.getAtpRsi()) == -1) {
                    conditionMatched++;
                    swing = dto.getNxtOptoHighPrcnt();
                    LOGGER.info("-----swing: {}, rsi: {}", swing, 0);
                    if(swing.compareTo(MIN_PROFIT) == 1) {
                        profitableTrades++;
                    }
                }
            }
        }
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

    public static void summarizeMinsuDel(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal LOWER_LIMIT = new BigDecimal(-13);
        BigDecimal UPPER_LIMIT = new BigDecimal(-8);
        BigDecimal MAX_RSI = new BigDecimal(30);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizeMinsuDel");
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {
            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                chg = dto.getDeliveryChgPrcnt();
                if(chg.compareTo(LOWER_LIMIT) == 1 && chg.compareTo(UPPER_LIMIT) == -1) {
                    conditionMatched++;
                    swing = dto.getNxtOptoLowPrcnt();
                    if(swing.compareTo(MIN_PROFIT) == 1) {
                        profitableTrades++;
                    }
                }
            }
        }
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

    public static void summarizeMinsuDelAndRsi(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal LOWER_LIMIT = new BigDecimal(-13);
        BigDecimal UPPER_LIMIT = new BigDecimal(-8);
        BigDecimal MAX_RSI = new BigDecimal(30);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizeMinsuDelAndRsi");
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {
            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                chg = dto.getDeliveryChgPrcnt();
                if(chg.compareTo(LOWER_LIMIT) == 1 && chg.compareTo(UPPER_LIMIT) == -1 && MAX_RSI.compareTo(dto.getAtpRsi()) == 1) {
                    conditionMatched++;
                    swing = dto.getNxtOptoLowPrcnt();
                    if(swing.compareTo(MIN_PROFIT) == 1) {
                        profitableTrades++;
                    }
                }
            }
        }
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

    public static void summarizeRsi(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal THIRTY = new BigDecimal(30);
        BigDecimal HUNDRED = new BigDecimal(100);
        float conditionMatched = 0;
        float profitableTrades = 0;
        BigDecimal chg = null;
        BigDecimal swing = null;
        int rowCtr = 0;
        LOGGER.info("summarizeRsi");
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {
            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                if(THIRTY.compareTo(dto.getAtpRsi()) == 1) {
                    conditionMatched++;
                    if(dto.getNxtOptoHighPrcnt().compareTo(MIN_PROFIT) == 1) {
                        profitableTrades++;
                    }
                }
            }
        }
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

    public static void summarizeDelPlusRsi(Map<String, List<DeliverySpikeDto>> symbolMap) {
        BigDecimal MIN_PROFIT = new BigDecimal(.50);
        BigDecimal ELEVEN = new BigDecimal(11);
        BigDecimal THIRTY = new BigDecimal(30);
        BigDecimal HUNDRED = new BigDecimal(100);
        float conditionMatched = 0;
        float profitableTrades = 0;
        int rowCtr = 0;
        LOGGER.info("summarizeDelPlusRsi");
        for(List<DeliverySpikeDto> dtos:symbolMap.values()) {
            for(DeliverySpikeDto dto:dtos) {
                if(dto.getNxtOptoHighPrcnt() == null) continue;
                rowCtr++;
                if(ELEVEN.compareTo(dto.getDeliveryChgPrcnt()) == 1 && THIRTY.compareTo(dto.getAtpRsi()) == 1) {
                    conditionMatched++;
                    if(dto.getNxtOptoHighPrcnt().compareTo(MIN_PROFIT) == 1) {
                        profitableTrades++;
                    }
                }
            }
        }
        LOGGER.info("total rows: {}", rowCtr);
        LOGGER.info("condition matched: {}", conditionMatched);
        LOGGER.info("profitable trades: {}", profitableTrades);
        float pct = profitableTrades / (conditionMatched / 100);
        LOGGER.info("profitable percentage: {}", pct) ;
    }

}
