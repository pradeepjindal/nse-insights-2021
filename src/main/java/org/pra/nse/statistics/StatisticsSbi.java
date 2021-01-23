package org.pra.nse.statistics;

import org.pra.nse.db.dto.DeliverySpikeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsSbi {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsSbi.class);

    public static void summarizeBucket(Map<String, List<DeliverySpikeDto>> symbolMap) {
        Map<String, Integer> bucket = new TreeMap<>();
        List<DeliverySpikeDto> sbiDtos = symbolMap.get("SBIN");
        BigDecimal intermediate;
        int ctr;
        for(DeliverySpikeDto dto:sbiDtos) {
            intermediate = dto.getAtp().divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP).multiply(BigDecimal.TEN);
            if(bucket.containsKey(intermediate.toString())) {
                ctr = bucket.get(intermediate.toString()).intValue() + 1;
                bucket.put(intermediate.toString(), ctr);
            } else {
                bucket.put(intermediate.toString(), 1);
            }
        }

        LOGGER.info("SBIN : buckets - {}", bucket.keySet());
        LOGGER.info("SBIN : entries - {}", bucket);
    }

    public static void summarizeDelivery(Map<String, List<DeliverySpikeDto>> symbolMap) {
        Map<String, Integer> bucket = new TreeMap<>();
        List<DeliverySpikeDto> sbiDtos = symbolMap.get("SBIN");
        BigDecimal intermediate = BigDecimal.ZERO;
        int ctr;
        BigDecimal lakh = new BigDecimal(100000);
        for(DeliverySpikeDto dto:sbiDtos) {
            intermediate = intermediate.add(dto.getDelivery());
            LOGGER.info("SBIN : td {}, atp {}, delivery {}, total {}",
                    dto.getTradeDate(),
                    dto.getAtp(),
                    dto.getDelivery().divide(lakh, 2, RoundingMode.HALF_UP),
                    intermediate.divide(lakh, 2, RoundingMode.HALF_UP));
        }
        LOGGER.info("SBIN : total delivery - {}", intermediate);
    }

}
