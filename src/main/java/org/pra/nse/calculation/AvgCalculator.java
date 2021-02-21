package org.pra.nse.calculation;

import org.pra.nse.ApCo;
import org.pra.nse.csv.data.AvgBean;
import org.pra.nse.csv.data.AvgCao;
import org.pra.nse.csv.data.CalcBean;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.service.DataServiceI;
import org.pra.nse.service.DateService;
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
public class AvgCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvgCalculator.class);

    private final String calc_name = CalcCons.AVG_FILE_PREFIX;
    private final String csv_header = CalcCons.AVG_CSV_HEADER_NEW;

    private final String computeFolderName = CalcCons.AVG_DIR_NAME_NEW;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final DataServiceI dataService;
    private final DateService dateService;

    public AvgCalculator(NseFileUtils nseFileUtils, PraFileUtils praFileUtils,
                         DataServiceI dataService,
                         DateService dateService) {
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.dataService = dataService;
        this.dateService = dateService;
    }

    public List<AvgBean> calculateAndReturn(LocalDate forDate) {
        List<AvgBean> beansMap = prepareData(forDate);
        List<CalcBean> calcBeanList = new ArrayList<>();
        beansMap.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSavingNew(forDate, calcBeanList, calc_name)) {
            return beansMap;
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

        List<AvgBean> beansMap = prepareData(forDate);
        List<CalcBean> calcBeanList = new ArrayList<>();
        beansMap.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSavingNew(forDate, calcBeanList, calc_name)) {
            saveToCsv(forDate, calcBeanList);
            //saveToDb(forDate, avgBeanList);
        }
    }

    private List<AvgBean> prepareData(LocalDate forDate) {
        int[] forDaysArray = {20, 15, 10, 5, 3};
        return prepareData(forDate, forDaysArray);
    }
    private List<AvgBean> prepareData(LocalDate forDate, int[] forDaysArray) {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(forDate.isAfter(latestNseDate)) return Collections.emptyList();

        Map<String, List<DeliverySpikeDto>> symbolMap;
        List<AvgBean> beans = new ArrayList<>();

        //int[] forDaysArray = {20, 10, 5, 3};
        for(int i=0; i<forDaysArray.length; i++) {
            int forDays = forDaysArray[i];
            LOGGER.info("{} calculating for {} days", calc_name, forDays);
            symbolMap = dataService.getRawDataBySymbol(forDate, forDays);
            List<AvgBean> list = loopIt(forDate, forDays, symbolMap);
            beans.addAll(list);
        }
        //
        return beans;
    }

    private List<AvgBean> loopIt(LocalDate forDate, int forDays,
                                 Map<String, List<DeliverySpikeDto>> symbolDtosMap) {
        List<AvgBean> beans = new ArrayList<>();
        symbolDtosMap.forEach( (mapSymbol, mapDtoList_OfGivenSymbol) -> {
            String listSymbol = mapDtoList_OfGivenSymbol.get(0).getSymbol();
            if(!mapSymbol.equals(listSymbol)) {
                throw new RuntimeException("symbol mismatch");
            }

            AvgBean bean = new AvgBean();
            bean.setSymbol(mapSymbol);
            bean.setTradeDate(forDate);
            bean.setForDays(forDays);

            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
//                        LOGGER.info("dt:{}, val:{}, del:{}, oi:{}", dto.getTradeDate(), dto.getVolume, dto.getDelivery, oiSumMap.get(dto.getSymbol());
                        return dto.getAtp();
                    },
                    (dto, calculatedValue) -> bean.setAtpSma(calculatedValue)
            );
            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
                        return dto.getVolume();
                    },
                    (dto, calculatedValue) -> bean.setVolSma(calculatedValue)
            );
            calculate(forDate, mapSymbol, mapDtoList_OfGivenSymbol,
                    dto -> {
                        return dto.getDelivery();
                    },
                    (dto, calculatedValue) -> bean.setDelSma(calculatedValue)
            );
//            calculate(forDate, symbol, list,
//                    dto -> {
//                        return dto.getOi();
//                    },
//                    (dto, calculatedValue) -> bean.setFoiSma(calculatedValue)
//            );
            //
            if(bean.getAtpSma() != null) beans.add(bean);
        });
        //
        return beans;
    }

    private void calculate(LocalDate forDate, String symbol,
                            List<DeliverySpikeDto> spikeDtoList,
                            Function<DeliverySpikeDto, BigDecimal> functionSupplier,
                            BiConsumer<DeliverySpikeDto, BigDecimal> biConsumer) {
        // calculate avg for each symbol
        //LOGGER.info("avg | for symbol = {}", symbol);
        BigDecimal zero = BigDecimal.ZERO;

        short ctr = 0;
        BigDecimal numberOfTrades = BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        DeliverySpikeDto latestDto = null;
        for(DeliverySpikeDto dsDto:spikeDtoList) {
            //LOGGER.info("loopDto = {}", dsDto.toFullCsvString());
            if(dsDto.getTradeDate().compareTo(forDate)  == 0) {
                latestDto = dsDto;
            }

            //if(dsDto.getTdycloseMinusYesclose().compareTo(zero) > 0)  {
            BigDecimal indicatorColumn = functionSupplier.apply(dsDto);
            if(indicatorColumn == null || indicatorColumn.compareTo(BigDecimal.ZERO) == 0) {
                indicatorColumn = BigDecimal.ZERO;
            } else {
                ctr++;
                sum = sum.add(indicatorColumn);
            }
        }
        if(ctr != spikeDtoList.size()) {
            LOGGER.warn("avg | for symbol = {}, ctr mismatch {}", symbol, ctr);
        }
        if(ctr == 0) {
            LOGGER.info("avg | for symbol = {}, ctr = {}", symbol, ctr);
        }

        //LOGGER.info("latestDto = {}", latestDto.toFullCsvString());
        BigDecimal avg = NumberUtils.divide(sum, new BigDecimal(ctr));
        //===========================================

        if(latestDto != null) biConsumer.accept(latestDto, avg);
        else LOGGER.warn("skipping avg, latestDto is null for symbol {}, may be phasing out from FnO", symbol);
        //LOGGER.info("for symbol = {}, avg = {}", symbol, avg);
    }

    private void saveToCsv(LocalDate forDate, List<CalcBean> beans) {
        String computeToFilePath = getComputeOutputPath(forDate);
        AvgCao.saveOverWrite(csv_header, beans, computeToFilePath, bean -> bean.toCsvString());
        LOGGER.info("{} | saved on disk ({})", calc_name, computeToFilePath);
    }

    private String getComputeOutputPath(LocalDate forDate) {
        String computeFileName = calc_name + forDate + ApCo.DATA_FILE_EXT;
        String computePath = ApCo.ROOT_DIR + File.separator + computeFolderName + File.separator + computeFileName;
        return computePath;
    }
}
