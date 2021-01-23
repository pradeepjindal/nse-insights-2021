package org.pra.nse.report;

import org.pra.nse.db.model.CalcMfiTabNew;
import org.pra.nse.db.model.CalcRsiTabNew;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class ReportUtils {

    public static Map<LocalDate, Map<String, CalcRsiTabNew>> transformRsiInto_TradeDateAndSymbol_DoubleMap(List<CalcRsiTabNew> rsiList) {
        Predicate<CalcRsiTabNew> predicate = dto -> true;
        // aggregate trade by symbols
        // tradeDateAndSymbolWise_DoubleMap
        Map<LocalDate, Map<String, CalcRsiTabNew>> localMap = new TreeMap<>();
        long rowCount = rsiList.stream()
                .filter(predicate)
                .map( filteredRow -> {
                    if(localMap.containsKey(filteredRow.getTradeDate())) {
                        if(localMap.get(filteredRow.getTradeDate()).containsKey(filteredRow.getSymbol())) {
//                            LOGGER.warn("tradeDate-symbol | matched tradeDate {} symbol {}", filteredRow.getTradeDate(), filteredRow.getSymbol());
                        } else {
                            localMap.get(filteredRow.getTradeDate()).put(filteredRow.getSymbol(), filteredRow);
                        }
                    } else {
//                        Map<String, DeliverySpikeDto> map = new HashMap<>();
                        Map<String, CalcRsiTabNew> map = new TreeMap<>();
                        map.put(filteredRow.getSymbol(), filteredRow);
                        localMap.put(filteredRow.getTradeDate(), map);
                        //LOGGER.info("tradeDate-symbol | tradeDate {}", filteredRow.getTradeDate());
                    }
                    return true;
                })
                .count();
        return localMap;
    }

    public static Map<LocalDate, Map<String, CalcMfiTabNew>> transformMfiInto_TradeDateAndSymbol_DoubleMap(List<CalcMfiTabNew> rsiList) {
        Predicate<CalcMfiTabNew> predicate = dto -> true;
        // aggregate trade by symbols
        // tradeDateAndSymbolWise_DoubleMap
        Map<LocalDate, Map<String, CalcMfiTabNew>> localMap = new TreeMap<>();
        long rowCount = rsiList.stream()
                .filter(predicate)
                .map( filteredRow -> {
                    if(localMap.containsKey(filteredRow.getTradeDate())) {
                        if(localMap.get(filteredRow.getTradeDate()).containsKey(filteredRow.getSymbol())) {
//                            LOGGER.warn("tradeDate-symbol | matched tradeDate {} symbol {}", filteredRow.getTradeDate(), filteredRow.getSymbol());
                        } else {
                            localMap.get(filteredRow.getTradeDate()).put(filteredRow.getSymbol(), filteredRow);
                        }
                    } else {
//                        Map<String, DeliverySpikeDto> map = new HashMap<>();
                        Map<String, CalcMfiTabNew> map = new TreeMap<>();
                        map.put(filteredRow.getSymbol(), filteredRow);
                        localMap.put(filteredRow.getTradeDate(), map);
                        //LOGGER.info("tradeDate-symbol | tradeDate {}", filteredRow.getTradeDate());
                    }
                    return true;
                })
                .count();
        return localMap;
    }

}
