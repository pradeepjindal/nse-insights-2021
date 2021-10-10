package org.pra.nse.report.pastPresentFuture;

import org.pra.nse.db.model.CalcMfiTab;
import org.pra.nse.db.model.CalcRsiTab;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class ReportUtils {

    public static Map<LocalDate, Map<String, CalcRsiTab>> transformRsiInto_TradeDateAndSymbol_DoubleMap(List<CalcRsiTab> rsiList) {
        Predicate<CalcRsiTab> predicate = dto -> true;
        // aggregate trade by symbols
        // tradeDateAndSymbolWise_DoubleMap
        Map<LocalDate, Map<String, CalcRsiTab>> localMap = new TreeMap<>();
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
                        Map<String, CalcRsiTab> map = new TreeMap<>();
                        map.put(filteredRow.getSymbol(), filteredRow);
                        localMap.put(filteredRow.getTradeDate(), map);
                        //LOGGER.info("tradeDate-symbol | tradeDate {}", filteredRow.getTradeDate());
                    }
                    return true;
                })
                .count();
        return localMap;
    }

    public static Map<LocalDate, Map<String, CalcMfiTab>> transformMfiInto_TradeDateAndSymbol_DoubleMap(List<CalcMfiTab> rsiList) {
        Predicate<CalcMfiTab> predicate = dto -> true;
        // aggregate trade by symbols
        // tradeDateAndSymbolWise_DoubleMap
        Map<LocalDate, Map<String, CalcMfiTab>> localMap = new TreeMap<>();
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
                        Map<String, CalcMfiTab> map = new TreeMap<>();
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
