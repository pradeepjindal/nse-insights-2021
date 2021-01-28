package org.pra.nse.service;

import org.pra.nse.Manager;
import org.pra.nse.db.dao.NseReportsDao;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.util.NumberUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DataService implements Manager, DataServiceI {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    private final PraFileUtils praFileUtils;
    private final NseReportsDao nseReportsDao;
    private final DataServiceHelper dataServiceHelper;
    private final DateService dateService;

    private final NavigableMap<Integer, LocalDate>  tradeDates_Asc_NavigableMap = new TreeMap<>();
    private final List<LocalDate>                   tradeDates_Desc_LinkedList = new LinkedList<>();
    private final Map<LocalDate, LocalDate>         nextDateMap = new TreeMap<>();
    private final Map<LocalDate, LocalDate>         backDateMap = new TreeMap<>();

    private List<DeliverySpikeDto> dbData = null;
    private Map<String, Map<LocalDate, DeliverySpikeDto>> dataMap_bySymbolAndTradeDate = new TreeMap<>();
    private Map<LocalDate, Map<String, DeliverySpikeDto>> dataMap_byTradeDateAndSymbol = new TreeMap<>();

    // materialized view date
    private boolean isDataInRawState = true;
    private LocalDate latestDbMvDate = null;
    private List<LocalDate> latest10Dates = null;
    private List<LocalDate> latest20Dates = null;

    public DataService(PraFileUtils praFileUtils,
                       NseReportsDao nseReportsDao,
                       DataServiceHelper dataServiceHelper,
                       DateService dateService) {
        this.praFileUtils = praFileUtils;
        this.nseReportsDao = nseReportsDao;
        this.dataServiceHelper = dataServiceHelper;
        this.dateService = dateService;
    }


    @Override
    public void execute() {
        LOGGER.info("Data Manger - Shop is open..........");
    }


    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, LocalDate minDate) {
        return getRawDataBySymbol(forDate, minDate, null);
    }
    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, LocalDate minDate, String forSymbol) {
        Predicate<DeliverySpikeDto> predicate = initializeRawData(forDate, minDate, forSymbol);
        return predicate == null ? Collections.EMPTY_MAP : dataServiceHelper.prepareDataBySymbol(dbData, predicate);
    }

    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, int forMinusDays) {
        return getRawDataBySymbol(forDate, forMinusDays, null);
    }
    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, int forMinusDays, String forSymbol) {
        Predicate<DeliverySpikeDto> predicate = initializeRawData(forDate, forMinusDays, forSymbol);
        return predicate == null ? Collections.EMPTY_MAP : dataServiceHelper.prepareDataBySymbol(dbData, predicate);
    }

    public Map<String, List<DeliverySpikeDto>> getRichDataBySymbol(LocalDate forDate, int forMinusDays) {
        return getRichDataBySymbol(forDate, forMinusDays, null);
    }
    public Map<String, List<DeliverySpikeDto>> getRichDataBySymbol(LocalDate forDate, int forMinusDays, String forSymbol) {
        Predicate<DeliverySpikeDto> predicate =  initializeData(forDate, forMinusDays, forSymbol);
        return predicate == null ? Collections.EMPTY_MAP : dataServiceHelper.prepareDataBySymbol(dbData, predicate);
    }

    public Map<LocalDate, Map<String, DeliverySpikeDto>> getRichDataByTradeDateAndSymbol(LocalDate forDate, int forMinusDays) {
        return getRichDataByTradeDateAndSymbol(forDate, forMinusDays, null);
    }
    public Map<LocalDate, Map<String, DeliverySpikeDto>> getRichDataByTradeDateAndSymbol(LocalDate forDate, int forMinusDays, String forSymbol) {
        Predicate<DeliverySpikeDto> predicate =  initializeData(forDate, forMinusDays, forSymbol);
        return predicate == null ? Collections.EMPTY_MAP : dataServiceHelper.prepareDataByTradeDateAndSymbol(dbData, predicate);
    }

    //
    private Predicate<DeliverySpikeDto> initializeRawData(LocalDate forDate, int forMinusDays, String forSymbol) {
        if(!dateService.validateTradeDate(forDate)) return null;
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(dbData == null || latestNseDate.isAfter(latestDbMvDate)) {
            bootUpRawData();
        }
        if(forDate.isAfter(latestDbMvDate))
            return null;
        else
            return predicateByDays(forDate, forMinusDays, forSymbol);
    }
    private Predicate<DeliverySpikeDto> initializeRawData(LocalDate forDate, LocalDate minDate, String forSymbol) {
        if(!dateService.validateTradeDate(forDate)) return null;
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(dbData == null || latestNseDate.isAfter(latestDbMvDate)) {
            bootUpRawData();
        }
        if(forDate.isAfter(latestDbMvDate))
            return null;
        else
            return predicateByDate(forDate, minDate, forSymbol);
    }

    private Predicate<DeliverySpikeDto> initializeData(LocalDate forDate, int forMinusDays, String forSymbol) {
        if(!dateService.validateTradeDate(forDate)) return null;
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCDF();
        if(dbData == null || latestNseDate.isAfter(latestDbMvDate)) {
            bootUpRawData();
            isDataInRawState = true;
        }
        if (isDataInRawState) {
            fillCalcFields();
            fillNextFields();
            //fillTheIndicators();
            isDataInRawState = false;
        }
        if(forDate.isAfter(latestDbMvDate))
            return null;
        else
            return predicateByDays(forDate, forMinusDays, forSymbol);
    }

    private Predicate<DeliverySpikeDto> predicateByDays(LocalDate forDate, int forMinusDays, String forSymbol) {
        //TODO what if minDate is null
        LocalDate minDate = minDate(forDate, forMinusDays);
        return predicateByDate(forDate, minDate, forSymbol);
    }
    private Predicate<DeliverySpikeDto> predicateByDate(LocalDate forDate, LocalDate minDate, String forSymbol) {
        //TODO what if minDate is null
        Predicate<DeliverySpikeDto> predicate = null;
        if (forSymbol == null) {
            predicate = dto -> filterDate(dto, minDate, forDate);
        } else {
            predicate = dto -> filterDateAndSymbol(dto, minDate, forDate, forSymbol);
        }
        return predicate;
    }


    private void bootUpRawData() {
        dbData = nseReportsDao.getDeliverySpikeTwo();
        dataMap_byTradeDateAndSymbol = dataServiceHelper.transformAllData_ByTradeDateAndSymbol(dbData);
        dataMap_bySymbolAndTradeDate = dataServiceHelper.transformAllData_BySymbolAndTradeDate(dbData);

        initializeMapOf_AscTradeDates();
        initializeMapOf_DescTradeDates();

        initializeTradeDates();
        initializeMapOf_BackTradeDates();
        initializeMapOf_NextTradeDates();

        if(!isFileCD_DatesAndDbDatesAreSame()) throw new RuntimeException("file nse dates and db mv dates does not match");

        dbData.forEach( row-> {
            row.setBackDate(backDateMap.get(row.getTradeDate()));
            row.setNextDate(nextDateMap.get(row.getTradeDate()));
        });

        dbData.forEach( row-> {
            if(dataMap_bySymbolAndTradeDate.get(row.getSymbol()) != null) {
                if(row.getBackDate() != null) {
                    DeliverySpikeDto backDateDto = dataMap_bySymbolAndTradeDate.get(row.getSymbol()).get(row.getBackDate());
                    row.setBackDto(backDateDto);
                }
                if(row.getNextDate() != null) {
                    DeliverySpikeDto nextDateDto = dataMap_bySymbolAndTradeDate.get(row.getSymbol()).get(row.getNextDate());
                    row.setNextDto(nextDateDto);
                }
            }
        });

        //
        //fillTheOi();
    }

    private void initializeTradeDates() {
        Set<LocalDate> tradeDateSet = new HashSet<>();
        dbData.forEach(row-> {
            tradeDateSet.add(row.getTradeDate());
        });
        List<LocalDate> tmpTradeDateList = tradeDateSet.stream().collect(Collectors.toList());
        Collections.sort(tmpTradeDateList, Collections.reverseOrder());

        latestDbMvDate = tmpTradeDateList.get(0);
        latest10Dates = tmpTradeDateList.stream().limit(10).collect(Collectors.toList());
        latest20Dates = tmpTradeDateList.stream().limit(20).collect(Collectors.toList());
    }

    private void initializeMapOf_AscTradeDates() {
        NavigableMap<LocalDate, LocalDate> tmpMap = new TreeMap<>();
        dbData.forEach(row-> {
            tmpMap.put(row.getTradeDate(), row.getTradeDate());
        });

        AtomicInteger dateCount = new AtomicInteger();
        tradeDates_Asc_NavigableMap.clear();
        tmpMap.keySet().stream().forEach( key -> tradeDates_Asc_NavigableMap.put(dateCount.incrementAndGet(), key));
        //tradeDates_Asc_NavigableMap.descendingMap();
    }
    private void initializeMapOf_DescTradeDates() {
        NavigableMap<LocalDate, LocalDate> tmpMap = new TreeMap<>();
        dbData.forEach(row-> {
            tmpMap.put(row.getTradeDate(), row.getTradeDate());
        });

        tradeDates_Desc_LinkedList.clear();
        tmpMap.descendingKeySet().stream().forEach( key -> tradeDates_Desc_LinkedList.add(key));
        //tradeDates_SortedLinkedList.reverse();
    }
    private void initializeMapOf_BackTradeDates() {
        for(int i = 0; i < tradeDates_Desc_LinkedList.size() - 1; i++) {
            //LOGGER.info("tdy: {}, nxt:{}", i+1, i);
            backDateMap.put(tradeDates_Desc_LinkedList.get(i), tradeDates_Desc_LinkedList.get(i+1));
            //LOGGER.info("tdy: {}, nxt:{}", tradeDates_Desc_LinkedList.get(i+1), tradeDates_Desc_LinkedList.get(i));
        }
    }
    private void initializeMapOf_NextTradeDates() {
        for(int i = 0; i < tradeDates_Desc_LinkedList.size() - 1; i++) {
            //LOGGER.info("tdy: {}, nxt:{}", i+1, i);
            nextDateMap.put(tradeDates_Desc_LinkedList.get(i+1), tradeDates_Desc_LinkedList.get(i));
            //LOGGER.info("tdy: {}, nxt:{}", tradeDates_Desc_LinkedList.get(i+1), tradeDates_Desc_LinkedList.get(i));
        }
    }

    private boolean isFileCD_DatesAndDbDatesAreSame() {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if (latestNseDate.equals(latestDbMvDate)) {
            return true;
        } else {
            return false;
        }
    }

    private LocalDate minDate(LocalDate forDate, int forMinusDays) {
        int fromIndex = tradeDates_Desc_LinkedList.indexOf(forDate);
        int toIndexDesc = fromIndex + forMinusDays -1;
        //LocalDate maxDate = tradeDates_Desc_LinkedList.get(fromIndex);
        if(toIndexDesc > tradeDates_Desc_LinkedList.size() -1) {
            int lastIndex = tradeDates_Desc_LinkedList.size() -1;
            throw new RuntimeException("more old data requited then the the available data " + tradeDates_Desc_LinkedList.get(lastIndex));
        }
        LocalDate minDate = tradeDates_Desc_LinkedList.get(toIndexDesc);
        LOGGER.info("forDate:{}, minusDays:{}, minDate:{}, maxDate:{}", forDate, forMinusDays, minDate, forDate);
        return minDate;
    }

    private boolean filterDateAndSymbol(DeliverySpikeDto dto, LocalDate minDate, LocalDate maxDate, String symbol) {
        //return filterDate(dto, minDate, maxDate) && symbol.toUpperCase().equals(dto.getSymbol());
        return filterDate(dto, minDate, maxDate) && filterSymbol(dto, symbol);
    }
    private boolean filterDate(DeliverySpikeDto dto, LocalDate minDate, LocalDate maxDate) {
        return dto.getTradeDate().isAfter(minDate.minusDays(1)) && dto.getTradeDate().isBefore(maxDate.plusDays(1));
    }
    private boolean filterSymbol(DeliverySpikeDto dto, String symbol) {
        return symbol.toUpperCase().equals(dto.getSymbol());
    }

    private void fillCalcFields() {
        LOGGER.info("DataManager - fillTheCalcFields");
        BigDecimal TWO = new BigDecimal(2);
        BigDecimal FOUR = new BigDecimal(4);
        BigDecimal HUNDRED = new BigDecimal(100);
        BigDecimal onePercent = null;

        BigDecimal ohlcSum = null;
        BigDecimal diff = null;
        BigDecimal highLowDiffByHalf = null;
        BigDecimal biggerValue = null;
        BigDecimal smallerValue = null;
        for(DeliverySpikeDto row: dbData) {
            //ohlc
            ohlcSum = row.getOpen().add(row.getHigh()).add(row.getLow()).add(row.getClose());
            row.setOhlc(NumberUtils.divide(ohlcSum, FOUR));

            //hld
            diff = row.getHigh().subtract(row.getLow());
            row.setHighLowDiff(diff);
            //hlm
            highLowDiffByHalf = NumberUtils.divide(diff, TWO);
            row.setHighLowMid(row.getLow().add(highLowDiffByHalf));
            //hlm-atp
            row.setAtpMhlm(row.getAtp().subtract(row.getHighLowMid()));

            //hlp
            onePercent = NumberUtils.onePercent(row.getOpen());
            row.setHighLowPct(NumberUtils.divide(diff, onePercent));

            //closeToLastPct
            if(row.getLast().compareTo(row.getClose()) == 1) {
                biggerValue = row.getLast();
                smallerValue = row.getClose();
                    onePercent = NumberUtils.onePercent(smallerValue);
                    diff = biggerValue.subtract(smallerValue);
                    row.setCloseToLastPercent(NumberUtils.divide(diff, onePercent));
            } else if (row.getClose().compareTo(row.getLast()) == 1) {
                biggerValue = row.getLast();
                smallerValue = row.getClose();
                    onePercent = NumberUtils.onePercent(smallerValue);
                    diff = biggerValue.subtract(smallerValue);
                    row.setCloseToLastPercent(NumberUtils.divide(diff, onePercent));
            } else {
                row.setCloseToLastPercent(BigDecimal.ZERO);
            }

            //vdr
            row.setVdr(NumberUtils.divide(row.getVolume(), row.getDelivery()));

        }
    }

    private void fillNextFields() {
        LOGGER.info("DataManager - fillTheNext");
        Predicate<DeliverySpikeDto> predicate = dto -> true;
        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolMap = dataServiceHelper.prepareDataByTradeDateAndSymbol(dbData, predicate);
        for(DeliverySpikeDto dto: dbData) {
            LocalDate nextDate = nextDateMap.get(dto.getTradeDate());
            //if(nextDate.compareTo(latestDbDate) == 1) continue;
            if(nextDate == null) continue;
            DeliverySpikeDto nextDto = tradeDateAndSymbolMap.get(nextDate).get(dto.getSymbol());
            if (nextDto == null) {
                LOGGER.warn("{} - no next data for: {} (may be symbol has phased out of fno)", dto.getSymbol(), nextDate);
            } else {
                dto.setNxtCloseToOpenPercent(nextDto.getCloseToOpenPercent());
                dto.setNxtOptoHighPrcnt(nextDto.getOthighPrcnt());
                dto.setNxtOptoLowPrcnt(nextDto.getOtlowPrcnt());
                dto.setNxtOptoAtpPrcnt(nextDto.getOtatpPrcnt());
            }
        }
    }

    private void fillTheNextOld() {
        LOGGER.info("DataManager - fillTheNext");
        LocalDate minDate = minDate(latestDbMvDate, 20);
        Predicate<DeliverySpikeDto> predicate = dto -> filterDate(dto, minDate, latestDbMvDate);
        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolMap = dataServiceHelper.prepareDataByTradeDateAndSymbol(dbData, predicate);
        //TODO use tradeDateAndSymbolMap instead of dbResults BUT dbResults keep the order while map not
        long ctr = dbData.stream()
                .filter( row -> row.getTradeDate().isAfter(tradeDates_Desc_LinkedList.get(20))
                && row.getTradeDate().isBefore(tradeDates_Desc_LinkedList.get(0)))
                .map( filteredRow -> {
                    LocalDate nextDate = nextDateMap.get(filteredRow.getTradeDate());
                    DeliverySpikeDto nextDto = tradeDateAndSymbolMap.get(nextDate).get(filteredRow.getSymbol());
                    if (nextDto == null) {
                        LOGGER.warn("{} - no next data for: {} (may be symbol has phased out of fno)", filteredRow.getSymbol(), nextDate);
                    } else {
                        filteredRow.setNxtCloseToOpenPercent(nextDto.getCloseToOpenPercent());
                        filteredRow.setNxtOptoHighPrcnt(nextDto.getOthighPrcnt());
                        filteredRow.setNxtOptoLowPrcnt(nextDto.getOtlowPrcnt());
                        filteredRow.setNxtOptoAtpPrcnt(nextDto.getOtatpPrcnt());
                    }
                    return true;
                }).count();
    }

//    private void fillTheIndicators() {
//        LocalDate minDate = minDate(latestDbDate, 21);
//        Predicate<DeliverySpikeDto> predicate = dto -> filterDate(dto, minDate, latestDbDate);
//        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolMap = prepareDataByTradeDateAndSymbol(predicate);
//
//        //load old Rsi
//        List<CalcRsiTab> oldRsiList = calcRsiRepository.findAll();
//        ReportHelper.enrichRsi(oldRsiList, tradeDateAndSymbolMap);
//
//        //load old Mfi
//        List<CalcMfiTab> oldMfiList = calcMfiRepository.findAll();
//        ReportHelper.enrichMfi(oldMfiList, tradeDateAndSymbolMap);
//
//        BigDecimal HUNDRED = new BigDecimal(100);
////        LocalDate minDate = minDate(latestDbDate, 20);
////        Predicate<DeliverySpikeDto> predicate = dto -> filterDate(dto, minDate, latestDbDate);
////        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolMap = prepareDataByTradeDateAndSymbol(predicate);
//        //TODO use tradeDateAndSymbolMap instead of dbResults BUT dbResults keep the order while map not
//        long ctr = dbResults.stream()
//                .filter( row -> row.getTradeDate().isAfter(tradeDates_Desc_LinkedList.get(20)))
//                .map( filteredRow -> {
//                    LocalDate backDate = backDateMap.get(filteredRow.getTradeDate());
//                    DeliverySpikeDto backDto = null;
//                    if(tradeDateAndSymbolMap.containsKey(backDate))
//                        backDto = tradeDateAndSymbolMap.get(backDate).get(filteredRow.getSymbol());
//                    if (backDto == null) {
//                        LOGGER.warn("{} - backDto is null for: {}", filteredRow.getSymbol(), backDate);
//                    } else if (backDto.getDelAtpMfi() == null) {
//                        LOGGER.warn("{} - DelAtpMfi is null for: {}", filteredRow.getSymbol(), backDate);
//                    } else if (backDto.getAtpRsi() == null) {
//                        LOGGER.warn("{} - AtpRsi is null for: {}", filteredRow.getSymbol(), backDate);
//                    } else {
//                        filteredRow.setDelAtpMfiChg(
//                                filteredRow.getDelAtpMfi().divide(
//                                        backDto.getDelAtpMfi().divide(HUNDRED, 2, RoundingMode.HALF_UP),
//                                        2, RoundingMode.HALF_UP
//                                )
//                        );
//                        filteredRow.setAtpRsiChg(
//                                filteredRow.getAtpRsi().divide(
//                                        backDto.getAtpRsi().divide(HUNDRED, 2, RoundingMode.HALF_UP),
//                                        2, RoundingMode.HALF_UP
//                                )
//                        );
//                    }
//                    return true;
//                }).count();
//    }

}
