package org.pra.nse.calculation;

import org.pra.nse.csv.data.CalcBeanNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CalcHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalcHelper.class);

    public static boolean validateForSavingNew(LocalDate forDate, List<CalcBeanNew> dtos, String filePrefix) {
        if (dtos == null || dtos.isEmpty()) {
            LOGGER.info("{} | saving skipped, no data supplied", filePrefix);
            return false;
        }
        Set<LocalDate> forDateSet = new HashSet<>();
        dtos.forEach( dto -> forDateSet.add(dto.getTradeDate()));
        if(forDateSet.size() != 1) {
            LOGGER.info("{} | saving skipped, discrepancy in the dates: {}", filePrefix, forDateSet);
            return false;
        }
        if(forDateSet.size() == 1 && forDate.compareTo(dtos.get(0).getTradeDate()) != 0) {
            LOGGER.info("{} | saving skipped, discrepancy in the data (forDate and dataDate not matching)", filePrefix);
            return false;
        }
        return true;
    }

}
