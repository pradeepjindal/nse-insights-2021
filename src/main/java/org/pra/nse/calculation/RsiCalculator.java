package org.pra.nse.calculation;

import org.pra.nse.ApCo;
import org.pra.nse.csv.data.CalcBean;
import org.pra.nse.csv.data.RsiBean;
import org.pra.nse.csv.data.RsiCao;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.service.DataServiceI;
import org.pra.nse.util.Du;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.NumberUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class RsiCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsiCalculator.class);

    private final String calc_name = CalcCons.RSI_FILE_PREFIX;
    private final String csv_header = CalcCons.RSI_CSV_HEADER_NEW;

    private final String computeFolderName = CalcCons.RSI_DIR_NAME;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final DataServiceI dataService;

    RsiCalculator(NseFileUtils nseFileUtils, PraFileUtils praFileUtils, DataServiceI dataService) {
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.dataService = dataService;
    }

    public List<RsiBean> calculateAndReturn(LocalDate forDate) {
        int[] forDaysArray = {20, 15, 10, 5, 3};
        return calculateAndReturn(forDate, forDaysArray, null);
    }
    public List<RsiBean> calculateAndReturn(LocalDate forDate, int forDays, String forSymbol) {
        return calculateAndReturn(forDate, new int[] {forDays}, forSymbol);
    }
    public List<RsiBean> calculateAndReturn(LocalDate forDate, int[] forDaysArray, String forSymbol) {
        List<RsiBean> beans = calculate_All_TimePeriods(forDate, forDaysArray, forSymbol);
        List<CalcBean> calcBeanList = new ArrayList<>();
        beans.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSaving(forDate, calcBeanList, calc_name)) {
            return beans;
        } else {
            return Collections.emptyList();
        }
    }

    public void calculateAndSave(LocalDate forDate) {
        String computeFilePath = getComputeOutputPath(forDate);
        LOGGER.info("{} | for:{}", calc_name, forDate.toString());
        if(nseFileUtils.isFileExist(computeFilePath)) {
            LOGGER.warn("{} already present (skipping calc and saving): {}", calc_name, computeFilePath);
            return;
        }

        List<RsiBean> beans = calculate_All_TimePeriods(forDate);
        List<CalcBean> calcBeanList = new ArrayList<>();
        beans.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSaving(forDate, calcBeanList, calc_name)) {
            saveToCsv(forDate, calcBeanList);
            //saveToDb(forDate, rsiBeanList);
        }
    }

    private List<RsiBean> calculate_All_TimePeriods(LocalDate forDate) {
        int[] forDaysArray = {20, 15, 10, 5, 3};
        return calculate_All_TimePeriods(forDate, forDaysArray, null);
    }
    private List<RsiBean> calculate_All_TimePeriods(LocalDate forDate, String forSymbol) {
        int[] forDaysArray = {20, 15, 10, 5, 3};
        return calculate_All_TimePeriods(forDate, forDaysArray, forSymbol);
    }
    private List<RsiBean> calculate_All_TimePeriods(LocalDate forDate, int[] forDaysArray) {
        return calculate_All_TimePeriods(forDate, forDaysArray, null);
    }
    private List<RsiBean> calculate_All_TimePeriods(LocalDate forDate, int[] forDaysArray, String forSymbol) {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(forDate.isAfter(latestNseDate)) return Collections.emptyList();

        Map<String, List<DeliverySpikeDto>> symbolMap;
        List<RsiBean> beans = new ArrayList<>();

        //int[] forDaysArray = {20, 10, 5, 3};
        for(int i=0; i<forDaysArray.length; i++) {
            int forDays = forDaysArray[i];
            LOGGER.info("{} calculating for {} days", calc_name, forDays);
            if(forSymbol == null)
                symbolMap = dataService.getRawDataBySymbol(forDate, forDays);
            else
                symbolMap = dataService.getRawDataBySymbol(forDate, forDays, forSymbol);
            List<RsiBean> list = calculate_Single_TimePeriod(forDate, forDays, symbolMap);
            beans.addAll(list);
        }
        //
        return beans;
    }

    private List<RsiBean> calculate_Single_TimePeriod(LocalDate forDate, int forDays, Map<String, List<DeliverySpikeDto>> symbolDtoMap) {
        List<RsiBean> beans = new ArrayList<>();
        RsiBean bean = null;
        String mapKeySymbol = null;
        List<DeliverySpikeDto> mapDtoList_OfGivenSymbol = null;
        for(Map.Entry<String, List<DeliverySpikeDto>> eachEntry :symbolDtoMap.entrySet()) {
            mapKeySymbol = eachEntry.getKey();
            mapDtoList_OfGivenSymbol = eachEntry.getValue();
            String listSymbol = mapDtoList_OfGivenSymbol.get(0).getSymbol();
            if(mapKeySymbol.equals(listSymbol))
                LOGGER.debug("{}: symbol matches [key({}) = val({})] (symbol-check-passed)", mapKeySymbol, mapKeySymbol, listSymbol);
            else {
                LOGGER.error("{}: symbol mis-match [key:({}) vs val:({})] (symbol-check-failed)", mapKeySymbol, mapKeySymbol, listSymbol);
                throw new RuntimeException("symbol mismatch");
            }

            LocalDate firstDataDate = mapDtoList_OfGivenSymbol.get(0).getTradeDate();
            int dataSize = mapDtoList_OfGivenSymbol.size();
            if(forDays == mapDtoList_OfGivenSymbol.size())
                LOGGER.debug("{}: [forDays({}) == spikeDtoList size ({})] (data-check-passed)", mapKeySymbol, forDays,dataSize);
            else {
                LOGGER.warn("{}: insufficient data [forDays({}) <> data size ({})], earliestDate={} (data-check-failed)",
                        mapKeySymbol, forDays, dataSize, firstDataDate);
                continue;
            }

            if(mapDtoList_OfGivenSymbol.get(0).getBackDto() == null) {
                LOGGER.warn("{}: insufficient data - backDto not found for first date {} (skipping-it)", mapKeySymbol, firstDataDate);
                continue;
            }

            bean = calculationBatch(forDate, forDays, mapKeySymbol, mapDtoList_OfGivenSymbol);
            if(bean.getAtpRsiSma() != null) beans.add(bean);
        }
        return beans;
    }

    private RsiBean calculationBatch(LocalDate forDate, int forDays, String mapKeySymbol, List<DeliverySpikeDto> mapDtoList_OfGivenSymbol) {
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

    private void calculateRsi(LocalDate forDate, int forDays, String symbol,
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
            BigDecimal rawPriceStrength = functionSupplier.apply(dsDto);
            if(rawPriceStrength == null || rawPriceStrength.compareTo(BigDecimal.ZERO) == 0)  {
                rawPriceStrength = BigDecimal.ZERO;
            } else if(rawPriceStrength.compareTo(BigDecimal.ZERO) == 1)  {
                upCtr++;
                upSum = upSum.add(rawPriceStrength);
                LOGGER.debug("up, rawPriceStrength {}, upSum {}, upCtr {}", rawPriceStrength, upSum, upCtr);
            } else if(rawPriceStrength.compareTo(BigDecimal.ZERO) == -1) {
                dnCtr++;
                dnSum = dnSum.add(rawPriceStrength);
                LOGGER.debug("dn, rawPriceStrength {}, dnSum {}, dnCtr {}", rawPriceStrength, dnSum, dnCtr);
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

    private void saveToCsv(LocalDate forDate, List<CalcBean> beans) {
        String computeToFilePath = getComputeOutputPath(forDate);
        RsiCao.saveOverWrite(csv_header, beans, computeToFilePath, bean -> bean.toCsvString());
        LOGGER.info("{} | saved on disk ({})", calc_name, computeToFilePath);
    }
    private String getComputeOutputPath(LocalDate forDate) {
        String computeFileName = calc_name + forDate + ApCo.DATA_FILE_EXT;
        String computePath = ApCo.ROOT_DIR + File.separator + computeFolderName + File.separator + computeFileName;
        return computePath;
    }

}
