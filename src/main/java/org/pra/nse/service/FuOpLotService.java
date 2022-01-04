package org.pra.nse.service;

import org.pra.nse.db.dao.NseOmDao;
import org.pra.nse.db.dto.LotSizeDto;
import org.pra.nse.refdata.LotSizeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FuOpLotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuOpLotService.class);

    private static List<LotSizeBean> lotSizeBeanList;
    private static Map<String, Long> lotSizeBeanMap;

    // symbol, trade_month, expiry_date, lot_size
    // symbol, expiry_date, trade_month, lot_size
    private static Map<String, Map<String, Map<String, Long>>> lotSizeMap = new HashMap<>();

    private NseOmDao omDao;

    public FuOpLotService(NseOmDao omDao) {
        this.omDao = omDao;
    }


    public long getLotSizeAsLong(String symbol, LocalDate tradeDate, LocalDate expiryDate) {
        if(lotSizeMap.size() == 0) {
            loadLotSizesFromDb();
        }
        return lotSizeMap.get(symbol).get(tradeDate.toString()).get(expiryDate.toString());
    }

    public BigDecimal getLotSizeAsBigDecimal(String symbol, LocalDate tradeDate, LocalDate expiryDate) {
        if(lotSizeMap.size() == 0) {
            loadLotSizesFromDb();
        }
        String tradeMonth = tradeDate.toString().substring(0,7);
        long lotSize = lotSizeMap.get(symbol).get(expiryDate.toString()).get(tradeMonth);
        return new BigDecimal(lotSize);
    }

    private void loadLotSizesFromDb() {
        List<LotSizeDto> lotSizeDtoList = omDao.getLotSizeList();

        String symbol = null;
        String expiryDate = null;
        String tradeMonth = null;
        Long lotSize = 0L;
        for(LotSizeDto dto: lotSizeDtoList) {
            symbol = dto.getSymbol();
            expiryDate = dto.getExpiryDate().toString();
            tradeMonth = dto.getTradeMonth();
            lotSize = dto.getLotSize();

            if(lotSizeMap.get(symbol) == null) {
                Map<String, Long> tradeMonthMap = getNewTradeDateMap(dto);
                Map<String, Map<String, Long>> expiryDateMap = getNewExpiryDateMap(dto, tradeMonthMap);
                lotSizeMap.put(symbol, expiryDateMap);
            } else {
                Map<String, Map<String, Long>> expiryDateMap = lotSizeMap.get(symbol);
                if(expiryDateMap.get(expiryDate) == null) {
                    Map<String, Long> tradeMonthMap = getNewTradeDateMap(dto);
                    expiryDateMap.put(expiryDate, tradeMonthMap);
                } else {
                    Map<String, Long> tradeMonthMap = expiryDateMap.get(expiryDate);
                    if(tradeMonthMap.get(tradeMonth) == null) {
                        tradeMonthMap.put(tradeMonth, lotSize);
                    } else {
                        LOGGER.warn("duplicate record, row", dto);
                    }
                }
            }
        }
    }

    Map<String, Long> getNewTradeDateMap(LotSizeDto lotSizeDto) {
        Map<String, Long> tradeMonthMap = new HashMap<>();
        tradeMonthMap.put(lotSizeDto.getTradeMonth(), lotSizeDto.getLotSize());
        return tradeMonthMap;
    }

    Map<String, Map<String, Long>> getNewExpiryDateMap(LotSizeDto lotSizeDto, Map<String, Long> tradeMonthMap) {
        Map<String, Map<String, Long>> expiryDateMap = new HashMap<>();
        expiryDateMap.put(lotSizeDto.getExpiryDate().toString(), tradeMonthMap);
        return expiryDateMap;
    }

}
