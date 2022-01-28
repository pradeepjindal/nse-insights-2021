package org.pra.nse.util;

import org.pra.nse.db.model.NseFoTab;
import org.pra.nse.db.repository.NseFoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LotSizeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotSizeUtil.class);

    public static Map<String, Map<LocalDate, Integer>> transform(NseFoRepo nseFoRepo, LocalDate forDate) {
        Map<String, Map<LocalDate, Integer>> symbol_ed_ls_map = new TreeMap<>();
        Iterable<NseFoTab> iterator = nseFoRepo.findByTradeDate(forDate);
        iterator.forEach( bean -> {
            if(symbol_ed_ls_map.containsKey(bean.getSymbol().trim())) {
                Map<LocalDate, Integer> ed_foBeans_map = symbol_ed_ls_map.get(bean.getSymbol().trim());
                if(ed_foBeans_map.containsKey(bean.getExpiryDate())) {
                    LOGGER.error("fo entry duplicate, symbol: {}, td: {}, ed: {}", bean.getSymbol().trim(), forDate, bean.getExpiryDate());
                } else {
                    ed_foBeans_map.put(bean.getExpiryDate(), bean.getLotSize());
                }
            } else {
                Map<LocalDate, Integer> ed_foBeans_map = new HashMap<>();
                ed_foBeans_map.put(bean.getExpiryDate(), bean.getLotSize());
                symbol_ed_ls_map.put(bean.getSymbol().trim(), ed_foBeans_map);
            }
        });
        return symbol_ed_ls_map;
    }
}
