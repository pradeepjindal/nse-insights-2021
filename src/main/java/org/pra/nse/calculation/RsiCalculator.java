package org.pra.nse.calculation;

import org.pra.nse.ApCo;
import org.pra.nse.csv.data.CalcBean;
import org.pra.nse.csv.data.RsiBean;
import org.pra.nse.csv.data.RsiCao;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.service.DataServiceI;
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

    private final String computeFolderName = CalcCons.RSI_DIR_NAME_NEW;

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
        List<RsiBean> beans = prepareData(forDate, forDaysArray);
        List<CalcBean> calcBeanList = new ArrayList<>();
        beans.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSavingNew(forDate, calcBeanList, calc_name)) {
            return beans;
        } else {
            return Collections.emptyList();
        }
    }

    public void calculateAndSave(LocalDate forDate) {
        String computeFilePath = getComputeOutputPath(forDate);
        LOGGER.info("{} | for:{}", calc_name, forDate.toString());
        if(nseFileUtils.isFileExist(computeFilePath)) {
            LOGGER.warn("{} already present (calculation and saving would be skipped): {}", calc_name, computeFilePath);
            return;
        }

        List<RsiBean> beans = prepareData(forDate);
        List<CalcBean> calcBeanList = new ArrayList<>();
        beans.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSavingNew(forDate, calcBeanList, calc_name)) {
            saveToCsv(forDate, calcBeanList);
            //saveToDb(forDate, rsiBeanList);
        }
    }

    private List<RsiBean> prepareData(LocalDate forDate) {
        int[] forDaysArray = {20, 10, 5, 3};
        return prepareData(forDate, forDaysArray);
    }
    private List<RsiBean> prepareData(LocalDate forDate, int[] forDaysArray) {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(forDate.isAfter(latestNseDate)) return Collections.emptyList();

        Map<String, List<DeliverySpikeDto>> symbolMap;
        List<RsiBean> beans = new ArrayList<>();

        //int[] forDaysArray = {20, 10, 5, 3};
        for(int i=0; i<forDaysArray.length; i++) {
            int forDays = forDaysArray[i];
            LOGGER.info("{} calculating for {} days", calc_name, forDays);
            symbolMap = dataService.getRawDataBySymbol(forDate, forDays);
            List<RsiBean> list = loopIt(forDate, forDays, symbolMap);
            beans.addAll(list);
        }
        //
        return beans;
    }

    private List<RsiBean> loopIt(LocalDate forDate, int forDays, Map<String, List<DeliverySpikeDto>> symbolDtoMap) {
        List<RsiBean> beans = new ArrayList<>();
        symbolDtoMap.forEach( (mapSymbol, mapDtoList_OfGivenSymbol) -> {
            String listSymbol = mapDtoList_OfGivenSymbol.get(0).getSymbol();
            if(!mapSymbol.equals(listSymbol)) {
                throw new RuntimeException("symbol mismatch");
            }

            RsiBean bean = new RsiBean();
            bean.setSymbol(mapSymbol);
            bean.setTradeDate(forDate);
            bean.setForDays(forDays);

            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
                        LOGGER.debug("calc+:{}", dto.getTdyatpMinusYesatp());
                        return dto.getTdyatpMinusYesatp();
                    },
                    (dto, calculatedValue) -> bean.setAtpRsiSma(calculatedValue)
            );
            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
                        return dto.getTdycloseMinusYesclose();
                    },
                    (dto, calculatedValue) -> bean.setCloseRsiSma(calculatedValue)
            );
            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
                        return dto.getTdylastMinusYeslast();
                    },
                    (dto, calculatedValue) -> bean.setLastRsiSma(calculatedValue)
            );
            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
                        return dto.getTdydelMinusYesdel();
                    },
                    (dto, calculatedValue) -> bean.setDelRsiSma(calculatedValue)
            );
            //
            if(bean.getAtpRsiSma() != null) beans.add(bean);
        });
        //
        return beans;
    }

    public void calculate(LocalDate forDate, String symbol,
                            List<DeliverySpikeDto> spikeDtoList,
                            Function<DeliverySpikeDto, BigDecimal> functionSupplier,
                            BiConsumer<DeliverySpikeDto,BigDecimal> biConsumer) {
//        if(spikeDtoList.size() != 10) {
//            LOGGER.warn("size of the dto list is not 10, it is {}, for {}", spikeDtoList.size(), spikeDtoList.get(0).getSymbol());
//        }

        //LOGGER.info("for symbol = {}", symbol);
        BigDecimal up = BigDecimal.ZERO;
        short upCtr = 0;
        BigDecimal dn = BigDecimal.ZERO;
        short dnCtr = 0;

        LOGGER.debug("spikeDtoList size {}", spikeDtoList.size());
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
                up = up.add(rawPriceStrength);
                LOGGER.debug("up, rawPriceStrength {}, up {}, upCtr {}", rawPriceStrength, up, upCtr);
            } else if(rawPriceStrength.compareTo(BigDecimal.ZERO) == -1) {
                dnCtr++;
                dn = dn.add(rawPriceStrength);
                LOGGER.debug("dn, rawPriceStrength {}, dn {}, dnCtr {}", rawPriceStrength, dn, dnCtr);
            } else {
                LOGGER.error("rsi | {}, UNKNOWN CONDITION", symbol);
            }
        }
        LOGGER.debug("rsi | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        if(upCtr == 0 || dnCtr == 0) {
//            LOGGER.warn("rsi | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }
//        if(upCtr == 3 && dnCtr == 0) {
//            LOGGER.info("rsi+ | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }
//        if(upCtr == 0 && dnCtr == 3) {
//            LOGGER.info("rsi- | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }

        //LOGGER.info("latestDto = {}", latestDto.toFullCsvString());
        //up = up.divide(upCtr == 0 ? BigDecimal.ONE : new BigDecimal(upCtr), 2, RoundingMode.HALF_UP);
        BigDecimal upAvg = NumberUtils.divide(up, new BigDecimal(upCtr));
        //dn = dn.divide(dnCtr == 0 ? BigDecimal.ONE : new BigDecimal(dnCtr), 2, RoundingMode.HALF_UP);
        BigDecimal dnAvg = NumberUtils.divide(dn, new BigDecimal(dnCtr));

        BigDecimal rs = BigDecimal.ZERO;
        if(up.compareTo(BigDecimal.ZERO) == 0 && dn.abs().compareTo(BigDecimal.ZERO) == 0) {
            LOGGER.warn("rsi | {}, Up and Dn both are Zero", symbol);
            rs = BigDecimal.ZERO;
        } else if (up.compareTo(BigDecimal.ZERO) == 1 && dn.abs().compareTo(BigDecimal.ZERO) == 0) {
            LOGGER.debug("rsi | {}, all closing are Up ({})", symbol, upAvg);
            //rs = up.divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
            rs = upAvg;
        } else if (up.compareTo(BigDecimal.ZERO) == 0 && dn.abs().compareTo(BigDecimal.ZERO) == 1) {
            LOGGER.debug("rsi | {}, all closing are Dn ({})", symbol, dnAvg);
            //rs = up.divide(dn.abs(), 2, RoundingMode.HALF_UP);
            rs = BigDecimal.ZERO;
        } else {
            LOGGER.debug("rsi | {}, Up and Dn both are non-Zero  ({})  ({})", symbol, upAvg, dnAvg);
            //rs = up.divide(dn.abs(), 2, RoundingMode.HALF_UP);
            rs = NumberUtils.divide(upAvg, dnAvg.abs());
        }
        LOGGER.debug("rsi | {}, (rs) ={}, upAvg = {}, dnAvg = {}", symbol, rs, upAvg, dnAvg);

        //rsi = 100 - (100 / (1 + rs));
        //------------------------------------------
        //(1 + rs)
        BigDecimal rsi = rs.add(BigDecimal.ONE);
        LOGGER.debug("rsi | {}, (1 + rs) = {}", symbol, rsi);
        //(100 / (1 + rs)
        rsi = NumberUtils.divide(NumberUtils.HUNDRED, rsi);
        LOGGER.debug("rsi | {}, (100 / (1 + rs) = {}", symbol, rsi);
        //100 - (100 / (1 + rs))
        rsi = NumberUtils.HUNDRED.subtract(rsi);
        LOGGER.debug("rsi | {}, 100 - (100 / (1 + rs)) = {}", symbol, rsi);
        //===========================================

        if(latestDto != null) biConsumer.accept(latestDto, rsi);
        else LOGGER.warn("skipping rsi, latestDto is null for symbol {}, may be phasing out from FnO", symbol);
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
