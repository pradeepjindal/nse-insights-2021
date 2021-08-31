package org.pra.nse.calculation;

import org.pra.nse.csv.data.RsiBean;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.util.Du;
import org.pra.nse.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RsiCalcLib {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsiCalcLib.class);

    public static RsiBean calculateRsiOnAtp(LocalDate forDate, int forDays, String mapKeySymbol,
                                            List<DeliverySpikeDto> mapDtoList_OfGivenSymbol) {
        RsiBean bean = new RsiBean();
        bean.setSymbol(mapKeySymbol);
        bean.setTradeDate(forDate);
        bean.setForDays(forDays);

        LOGGER.debug("calc-AtpRsi");
        calculateRsi(forDate, forDays, mapKeySymbol, mapDtoList_OfGivenSymbol,
                dto -> {
                    LOGGER.debug("calc-AtpRsi, TdyAtp-YesAtp: {}, (tradeDate: {})", dto.getTdyatpMinusYesatp(), dto.getTradeDate());
                    LOGGER.debug("calc-AtpRsi, TdyAtp - YesAtp : {} - {} = {}",
                            dto.getAtp(), dto.getBackDto().getAtp(), dto.getAtp().subtract(dto.getBackDto().getAtp()));
                    return dto.getTdyatpMinusYesatp();
                },
                (dto, calculatedValue) -> bean.setAtpRsiSma(calculatedValue)
        );

        return bean;
    }

    public static RsiBean calculateRsiOnClose(LocalDate forDate, int forDays, String mapKeySymbol,
                                              List<DeliverySpikeDto> mapDtoList_OfGivenSymbol) {
        RsiBean bean = new RsiBean();
        bean.setSymbol(mapKeySymbol);
        bean.setTradeDate(forDate);
        bean.setForDays(forDays);

        LOGGER.debug("calc-CloseRSi");
        calculateRsi(forDate, forDays, mapKeySymbol, mapDtoList_OfGivenSymbol,
                dto -> {
                    LOGGER.debug("calc-CloseRSi, Tdyclose-Yesclose: {}, (tradeDate: {})", dto.getTdycloseMinusYesclose(), dto.getTradeDate());
                    LOGGER.debug("calc-CloseRSi, Tdyclose - Yesclose : {} - {} = {}",
                            dto.getClose(), dto.getBackDto().getClose(), dto.getClose().subtract(dto.getBackDto().getClose()));
                    return dto.getTdycloseMinusYesclose();
                },
                (dto, calculatedValue) -> bean.setCloseRsiSma(calculatedValue)
        );

        return bean;
    }

    public static RsiBean calculateRsiOnLast(LocalDate forDate, int forDays, String mapKeySymbol,
                                             List<DeliverySpikeDto> mapDtoList_OfGivenSymbol) {
        RsiBean bean = new RsiBean();
        bean.setSymbol(mapKeySymbol);
        bean.setTradeDate(forDate);
        bean.setForDays(forDays);

        LOGGER.debug("calc-LastRsi");
        calculateRsi(forDate, forDays, mapKeySymbol, mapDtoList_OfGivenSymbol,
                dto -> {
                    LOGGER.debug("calc-LastRsi, Tdylast-Yeslast: {}, (tradeDate: {})", dto.getTdylastMinusYeslast(), dto.getTradeDate());
                    LOGGER.debug("calc-LastRsi, Tdylast - Yeslast : {} - {} = {}",
                            dto.getLast(), dto.getBackDto().getLast(), dto.getLast().subtract(dto.getBackDto().getLast()));
                    return dto.getTdylastMinusYeslast();
                },
                (dto, calculatedValue) -> bean.setLastRsiSma(calculatedValue)
        );

        return bean;
    }

    public static RsiBean calculateRsiOnDelivery(LocalDate forDate, int forDays, String mapKeySymbol,
                                                 List<DeliverySpikeDto> mapDtoList_OfGivenSymbol) {
        RsiBean bean = new RsiBean();
        bean.setSymbol(mapKeySymbol);
        bean.setTradeDate(forDate);
        bean.setForDays(forDays);

        LOGGER.debug("calc-DelRsi");
        calculateRsi(forDate, forDays, mapKeySymbol, mapDtoList_OfGivenSymbol,
                dto -> {
                    LOGGER.debug("calc-DelRsi, Tdydel-Yesdel:{ {}, (tradeDate: {})", dto.getTdydelMinusYesdel(), dto.getTradeDate());
                    LOGGER.debug("calc-DelRsi, TdyDelivery - YesDelivery : {} - {} = {}",
                            dto.getDelivery(), dto.getBackDto().getDelivery(), dto.getDelivery().subtract(dto.getBackDto().getDelivery()));
                    return dto.getTdydelMinusYesdel();
                },
                (dto, calculatedValue) -> bean.setDelRsiSma(calculatedValue)
        );

        return bean;
    }


    public static void calculateRsi(LocalDate forDate, int forDays, String symbol,
                              List<DeliverySpikeDto> spikeDtoList,
                              Function<DeliverySpikeDto, BigDecimal> functionSupplier,
                              BiConsumer<DeliverySpikeDto,BigDecimal> biConsumer) {
//        if(spikeDtoList.size() != 10) {
//            LOGGER.warn("size of the dto list is not 10, it is {}, for {}", spikeDtoList.size(), spikeDtoList.get(0).getSymbol());
//        }

        //LOGGER.info("for symbol = {}", symbol);
        BigDecimal upSum = BigDecimal.ZERO;
        short upCtr = 0;
        BigDecimal dnSum = BigDecimal.ZERO;
        short dnCtr = 0;

        DeliverySpikeDto latestDto = null;
        for(DeliverySpikeDto dsDto:spikeDtoList) {
            //LOGGER.info("loopDto = {}", dsDto.toFullCsvString());
            if(dsDto.getTradeDate().compareTo(forDate)  == 0) {
                latestDto = dsDto;
            }

            //if(dsDto.getTdycloseMinusYesclose().compareTo(zero) > 0)  {
            BigDecimal changeInPrice = functionSupplier.apply(dsDto);
            if(changeInPrice == null || changeInPrice.compareTo(BigDecimal.ZERO) == 0)  {
                changeInPrice = BigDecimal.ZERO;
            } else if(changeInPrice.compareTo(BigDecimal.ZERO) == 1)  {
                upCtr++;
                upSum = upSum.add(changeInPrice);
                LOGGER.debug("up, changeInPrice {}, upSum {}, upCtr {}", changeInPrice, upSum, upCtr);
            } else if(changeInPrice.compareTo(BigDecimal.ZERO) == -1) {
                dnCtr++;
                dnSum = dnSum.add(changeInPrice);
                LOGGER.debug("dn, changeInPrice {}, dnSum {}, dnCtr {}", changeInPrice, dnSum, dnCtr);
            } else {
                LOGGER.error("rsi | {}, UNKNOWN CONDITION", Du.symbol(symbol));
            }
        }
        LOGGER.debug("rsi | {}, forDate={}, upCtr={}, upSum={}, dnCtr={}, dnSum={}", Du.symbol(symbol), forDate, upCtr, upSum, dnCtr, dnSum);
//        if(upCtr == 0 || dnCtr == 0) {
//            LOGGER.warn("rsi | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }
//        if(upCtr == 3 && dnCtr == 0) {
//            LOGGER.info("rsi+ | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }
//        if(upCtr == 0 && dnCtr == 3) {
//            LOGGER.info("rsi- | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }

        BigDecimal upAvg = NumberUtils.divide(upSum, new BigDecimal(forDays));
        BigDecimal dnAvg = NumberUtils.divide(dnSum, new BigDecimal(forDays));
        LOGGER.debug("rsi | {}, [avg/{}] upAvg = {}, dnAvg = {}", Du.symbol(symbol), forDays, upAvg, dnAvg);

        BigDecimal dnSumAbsolute = dnSum.abs();
        BigDecimal rs = BigDecimal.ZERO;
        if(upSum.compareTo(BigDecimal.ZERO) == 0 && dnSum.abs().compareTo(BigDecimal.ZERO) == 0) {
            LOGGER.warn("rsi | {}, UpAvg and DnAvg both are Zero", Du.symbol(symbol));
            rs = BigDecimal.ZERO;
        } else if (upSum.compareTo(BigDecimal.ZERO) == 1 && dnSum.abs().compareTo(BigDecimal.ZERO) == 0) {
            LOGGER.debug("rsi | {}, all closing are Up, Avg = ({})", Du.symbol(symbol), upAvg);
            rs = upAvg;
        } else if (upSum.compareTo(BigDecimal.ZERO) == 0 && dnSum.abs().compareTo(BigDecimal.ZERO) == 1) {
            LOGGER.debug("rsi | {}, all closing are Dn, Avg = ({})", Du.symbol(symbol), dnAvg);
            rs = BigDecimal.ZERO;
        } else {
            LOGGER.debug("rsi | {}, UpAvg and DnAvg both are non-Zero  ({})  ({})", Du.symbol(symbol), upAvg, dnAvg);
            rs = NumberUtils.divide(upAvg, dnAvg.abs());
        }
        LOGGER.debug("rsi | {}, [rs = upAvg/dnAvg.abs] = {}, upAvg = {}, dnAvg = {}", Du.symbol(symbol), rs, upAvg, dnAvg);

        //rsi = 100 - (100 / (1 + rs));
        //------------------------------------------
        //(1 + rs)
        BigDecimal rsi = rs.add(BigDecimal.ONE);
        LOGGER.debug("rsi | {}, [rsi = 1+rs] = {}", Du.symbol(symbol), rsi);
        //(100 / (1 + rs)
        rsi = NumberUtils.divide(NumberUtils.HUNDRED, rsi);
        LOGGER.debug("rsi | {}, [rsi = 100 / (1+ rs)] = {}", Du.symbol(symbol), rsi);
        //100 - (100 / (1 + rs))
        rsi = NumberUtils.HUNDRED.subtract(rsi);
        LOGGER.debug("rsi | {}, [rsi = 100 - (100 / (1+rs))] = {}", Du.symbol(symbol), rsi);
        //==============================================

        if(latestDto != null) biConsumer.accept(latestDto, rsi);
        else LOGGER.warn("skipping rsi, latestDto is null for {}, may be phasing out from FnO", Du.symbol(symbol));
        //LOGGER.info("for symbol = {}, rsi = {}", symbol, rsi);
    }

}
