package org.pra.nse.calculation;

import org.pra.nse.ApCo;
import org.pra.nse.csv.data.CalcBeanNew;
import org.pra.nse.csv.data.MfiBeanNew;
import org.pra.nse.csv.data.MfiCaoNew;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.service.DataService;
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
public class MfiCalculatorNew {
    private static final Logger LOGGER = LoggerFactory.getLogger(MfiCalculatorNew.class);

    private final String calc_name = CalcCons.MFI_FILE_PREFIX;
    private final String csv_header = CalcCons.MFI_CSV_HEADER_NEW;

    private final String computeFolderName = CalcCons.MFI_DIR_NAME_NEW;


    private final DataService dataService;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;


    public MfiCalculatorNew(DataService dataService,
                            NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        this.dataService = dataService;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }


    public List<MfiBeanNew> calculateAndReturn(LocalDate forDate) {
        int[] forDaysArray = {20, 10, 5, 3};
        List<MfiBeanNew> beans = prepareData(forDate, forDaysArray);
        List<CalcBeanNew> calcBeanList = new ArrayList<>();
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

        List<MfiBeanNew> beans = prepareData(forDate);
        List<CalcBeanNew> calcBeanList = new ArrayList<>();
        beans.forEach( bean -> {
            calcBeanList.add(bean);
        });
        if(CalcHelper.validateForSavingNew(forDate, calcBeanList, calc_name)) {
            saveToCsv(forDate, calcBeanList);
            //saveToDb(forDate, mfiBeanList);
        }
    }

    private List<MfiBeanNew> prepareData(LocalDate forDate) {
        int[] forDaysArray = {20, 10, 5, 3};
        return prepareData(forDate, forDaysArray);
    }
    private List<MfiBeanNew> prepareData(LocalDate forDate, int[] forDaysArray) {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(forDate.isAfter(latestNseDate)) return Collections.emptyList();

        Map<String, List<DeliverySpikeDto>> symbolMap;
        List<MfiBeanNew> beans = new ArrayList<>();

        //int[] forDaysArray = {20, 10, 5, 3};
        for(int i=0; i<forDaysArray.length; i++) {
            int forDays = forDaysArray[i];
            LOGGER.info("{} calculating for {} days", calc_name, forDays);
            symbolMap = dataService.getRawDataBySymbol(forDate, forDays);
            List<MfiBeanNew> list = loopIt(forDate, forDays, symbolMap);
            beans.addAll(list);
        }
        //
        return beans;
    }

    private List<MfiBeanNew> loopIt(LocalDate forDate, int forDays, Map<String, List<DeliverySpikeDto>> symbolDtoMap) {
        //List<DeliverySpikeDto> dtos_ToBeSaved = new ArrayList<>();
        //Map<String, MfiBeanNew> beansMap = new HashMap<>();
        List<MfiBeanNew> beans = new ArrayList<>();
        symbolDtoMap.forEach( (symbol, list) -> {
            MfiBeanNew bean = new MfiBeanNew();
            bean.setSymbol(list.get(0).getSymbol());
            bean.setTradeDate(forDate);
            bean.setForDays(forDays);

            calculate(forDate, symbol, list,
                    dto -> {
//                        if(dto.getSymbol().equals("INDUSINDBK")) {
//                            LOGGER.info("");
//                        }
                        //LOGGER.info("sym:{}, dt:{}, atp:{}, val:{}", dto.getSymbol(), dto.getTradeDate(), dto.getAtp(), dto.getVolume());
                        //return dto.getVolume();
                        if(dto.getAtpChgPrcnt().compareTo(BigDecimal.ZERO) == 1) {
                            //LOGGER.info("calc+:{}", dto.getAtp().multiply(dto.getVolume()));
                            return dto.getAtp().multiply(dto.getVolume());
                        } else {
                            //LOGGER.info("calc-:{}", dto.getAtp().multiply(dto.getVolume()).multiply(new BigDecimal(-1)));
                            return dto.getAtp().multiply(dto.getVolume()).multiply(new BigDecimal(-1));
                        }
                    },
                    (dto, calculatedValue) -> bean.setVolMfi(calculatedValue)
            );
            calculate(forDate, symbol, list,
                    dto -> {
                        //LOGGER.info("sym:{}, dt:{}, atp:{}, del:{}", dto.getSymbol(), dto.getTradeDate(), dto.getAtp(), dto.getDelivery());
                        //return dto.getDelivery();
                        if(dto.getAtpChgPrcnt().compareTo(BigDecimal.ZERO) == 1) {
                            //LOGGER.info("calc+:{}", dto.getAtp().multiply(dto.getDelivery()));
                            return dto.getAtp().multiply(dto.getDelivery());
                        } else {
                            //LOGGER.info("calc-:{}", dto.getAtp().multiply(dto.getDelivery()).multiply(new BigDecimal(-1)));
                            return dto.getAtp().multiply(dto.getDelivery()).multiply(new BigDecimal(-1));
                        }
                    },
                    (dto, calculatedValue) -> bean.setDelMfi(calculatedValue)
            );
            //
            if(bean.getVolMfi() != null) beans.add(bean);
        });
        //
        return beans;
    }

    public void calculate(LocalDate forDate, String symbol,
                            List<DeliverySpikeDto> spikeDtoList,
                            Function<DeliverySpikeDto, BigDecimal> functionSupplier,
                            BiConsumer<DeliverySpikeDto, BigDecimal> biConsumer) {
//        if(spikeDtoList.size() != 10) {
//            LOGGER.warn("size of the dto list is not 10, it is {}, for {}", spikeDtoList.size(), spikeDtoList.get(0).getSymbol());
//        }

        //LOGGER.info("mfi | for symbol = {}", symbol);
        short upCtr = 0;
        BigDecimal up = BigDecimal.ZERO;
        short dnCtr = 0;
        BigDecimal dn = BigDecimal.ZERO;

        LOGGER.debug("spikeDtoList size {}", spikeDtoList.size());
        DeliverySpikeDto latestDto = null;
        for(DeliverySpikeDto dsDto:spikeDtoList) {
            //LOGGER.info("loopDto = {}", dsDto.toFullCsvString());
            if(dsDto.getTradeDate().compareTo(forDate)  == 0) {
                latestDto = dsDto;
            }

            BigDecimal rawMoneyFlow = functionSupplier.apply(dsDto);
            if(rawMoneyFlow == null || rawMoneyFlow.compareTo(BigDecimal.ZERO) == 0) {
                rawMoneyFlow = BigDecimal.ZERO;
            } else if(rawMoneyFlow.compareTo(BigDecimal.ZERO) == 1)  {
                up = up.add(rawMoneyFlow);
                upCtr++;
            } else if (rawMoneyFlow.compareTo(BigDecimal.ZERO) == -1) {
                dn = dn.add(rawMoneyFlow.abs());
                dnCtr++;
            } else {
                LOGGER.error("mfi | {}, UNKNOWN CONDITION", symbol);
            }
        }
//        if(upCtr == 0 || dnCtr == 0) {
//            LOGGER.warn("mfi | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
//        }
        if(upCtr == 3 && dnCtr == 0) {
            LOGGER.info("mfi+ | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
        }
        if(upCtr == 0 && dnCtr == 3) {
            LOGGER.info("mfi- | forSymbol = {}, forDate = {}, upCtr = {}, dnCtr = {}", symbol, forDate, upCtr, dnCtr);
        }

        BigDecimal moneyFlowRatio;
        if(upCtr == 0 && dnCtr == 0) {
            moneyFlowRatio = BigDecimal.ZERO;
        } else if(upCtr == 1 && dnCtr == 0) {
            moneyFlowRatio = up;
        } else if(upCtr == 0 && dnCtr == 1) {
            moneyFlowRatio = BigDecimal.ZERO;
        } else {
            moneyFlowRatio = NumberUtils.divide(up, dn);
        }
        //mfi = 100 - (100 / (1 + moneyFlowRatio));
        //------------------------------------------
        //(1 + rs)
        BigDecimal mfi = moneyFlowRatio.add(BigDecimal.ONE);
        //(100 / (1 + rs)
        mfi = NumberUtils.divide(NumberUtils.HUNDRED, mfi);
        //100 - (100 / (1 + rs))
        mfi = NumberUtils.HUNDRED.subtract(mfi);
        //===========================================

        if(latestDto != null) biConsumer.accept(latestDto, mfi);
        else LOGGER.warn("skipping mfi, latestDto is null symbol {}, dt {}, may be phasing out from FnO", symbol, forDate);
        //LOGGER.info("for symbol = {}, mfi = {}", symbol, mfi);
    }

    private void saveToCsv(LocalDate forDate, List<CalcBeanNew> beans) {
        String computeToFilePath = getComputeOutputPath(forDate);
        MfiCaoNew.saveOverWrite(csv_header, beans, computeToFilePath, bean -> bean.toCsvString());
        LOGGER.info("{} | saved on disk ({})", calc_name, computeToFilePath);
    }

    private String getComputeOutputPath(LocalDate forDate) {
        String computeFileName = calc_name + forDate + ApCo.DATA_FILE_EXT;
        String computePath = ApCo.ROOT_DIR + File.separator + computeFolderName + File.separator + computeFileName;
        return computePath;
    }
}
