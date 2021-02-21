package org.pra.nse.service;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.refdata.RefData;
import org.pra.nse.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

@Component
public class DataServiceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataServiceHelper.class);

    public Map<LocalDate, Map<String, DeliverySpikeDto>> transformAllData_ByTradeDateAndSymbol(List<DeliverySpikeDto> dbData) {
        Predicate<DeliverySpikeDto> filterPredicate = dto -> true;
        Map<LocalDate, Map<String, DeliverySpikeDto>> localMap = new TreeMap<>();
        long rowCount = dbData.stream()
                .filter( filterPredicate )
                .map( filteredRow -> {
                    if(localMap.containsKey(filteredRow.getTradeDate())) {
                        if(localMap.get(filteredRow.getTradeDate()).containsKey(filteredRow.getSymbol())) {
                            LOGGER.warn("tradeDate-symbol | matched tradeDate {} symbol {}", filteredRow.getTradeDate(), filteredRow.getSymbol());
                        } else {
                            localMap.get(filteredRow.getTradeDate()).put(filteredRow.getSymbol(), filteredRow);
                        }
                    } else {
                        Map<String, DeliverySpikeDto> map = new TreeMap<>();
                        map.put(filteredRow.getSymbol(), filteredRow);
                        localMap.put(filteredRow.getTradeDate(), map);
                        //LOGGER.info("tradeDate-symbol | tradeDate {}", filteredRow.getTradeDate());
                    }
                    return true;
                })
                .count();
        return localMap;
    }

    public Map<String, Map<LocalDate, DeliverySpikeDto>> transformAllData_BySymbolAndTradeDate(List<DeliverySpikeDto> dbData) {
        Predicate<DeliverySpikeDto> filterPredicate = dto -> true;
        Map<String, Map<LocalDate, DeliverySpikeDto>> localMap = new TreeMap<>();
        long rowCount = dbData.stream()
                .filter( filterPredicate )
                .map( filteredRow -> {
                    if(localMap.containsKey(filteredRow.getSymbol())) {
                        if(localMap.get(filteredRow.getSymbol()).containsKey(filteredRow.getTradeDate())) {
                            LOGGER.warn("symbol-tradeDate | matched symbol {} tradeDate {}", filteredRow.getSymbol(), filteredRow.getTradeDate());
                        } else {
                            localMap.get(filteredRow.getSymbol()).put(filteredRow.getTradeDate(), filteredRow);
                        }
                    } else {
//                        Map<String, DeliverySpikeDto> map = new HashMap<>();
                        Map<LocalDate, DeliverySpikeDto> map = new TreeMap<>();
                        map.put(filteredRow.getTradeDate(), filteredRow);
                        localMap.put(filteredRow.getSymbol(), map);
                        //LOGGER.info("symbol-tradeDate | tradeDate {}", filteredRow.getTradeDate());
                    }
                    return true;
                })
                .count();
        return localMap;
    }

    //
    public Map<String, List<DeliverySpikeDto>> prepareDataBySymbol(List<DeliverySpikeDto> dbData,
                                                                    Predicate<DeliverySpikeDto> filterPredicate) {
        // aggregate trade by symbols
        // symbol wise trade list
        Map<String, List<DeliverySpikeDto>> localMap = new TreeMap<>();
        long rowCount = dbData.stream()
                .filter( filterPredicate )
                //.filter( passedRow -> "ACC".toUpperCase().equals(passedRow.getSymbol()))
                .map( filteredRow -> {
                    if(localMap.containsKey(filteredRow.getSymbol())) {
                        localMap.get(filteredRow.getSymbol()).add(filteredRow);
                    } else {
                        List<DeliverySpikeDto> list = new LinkedList<>();
                        list.add(filteredRow);
                        localMap.put(filteredRow.getSymbol(), list);
                    }
                    return true;
                })
                .count();
        return localMap;
    }
    public Map<LocalDate, Map<String, DeliverySpikeDto>> prepareDataByTradeDateAndSymbol(List<DeliverySpikeDto> dbData,
                                                                                          Predicate<DeliverySpikeDto> filterPredicate) {
        Map<LocalDate, Map<String, DeliverySpikeDto>> localMap = new TreeMap<>();
        long rowCount = dbData.stream()
                .filter( filterPredicate )
                .map( filteredRow -> {
                    if(localMap.containsKey(filteredRow.getTradeDate())) {
                        if(localMap.get(filteredRow.getTradeDate()).containsKey(filteredRow.getSymbol())) {
                            LOGGER.warn("tradeDate-symbol | matched tradeDate {} symbol {}", filteredRow.getTradeDate(), filteredRow.getSymbol());
                        } else {
                            localMap.get(filteredRow.getTradeDate()).put(filteredRow.getSymbol(), filteredRow);
                        }
                    } else {
//                        Map<String, DeliverySpikeDto> map = new HashMap<>();
                        Map<String, DeliverySpikeDto> map = new TreeMap<>();
                        map.put(filteredRow.getSymbol(), filteredRow);
                        localMap.put(filteredRow.getTradeDate(), map);
                        //LOGGER.info("tradeDate-symbol | tradeDate {}", filteredRow.getTradeDate());
                    }
                    return true;
                })
                .count();
        return localMap;
    }

    void enrichWithFutureLotData(List<DeliverySpikeDto> dbData) {
        dbData.forEach( dto -> {
            long lotSize = RefData.getLotSize(dto.getSymbol());
            dto.setLotSize(lotSize);
        });
    }

}
