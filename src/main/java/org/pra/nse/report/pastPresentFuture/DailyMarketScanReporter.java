package org.pra.nse.report.pastPresentFuture;

import org.pra.nse.ApCo;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.db.model.CalcAvgTab;
import org.pra.nse.db.model.CalcMfiTab;
import org.pra.nse.db.model.CalcRsiTab;
import org.pra.nse.db.repository.CalcAvgRepo;
import org.pra.nse.db.repository.CalcMfiRepo;
import org.pra.nse.db.repository.CalcRsiRepo;
import org.pra.nse.email.EmailService;
import org.pra.nse.refdata.FmCategoryEnum;
import org.pra.nse.refdata.LotSizeService;
import org.pra.nse.service.DataServiceI;
import org.pra.nse.service.DateService;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.DirUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.pra.nse.report.pastPresentFuture.ReportConstants.DMS;
import static org.pra.nse.report.pastPresentFuture.ReportConstants.DAILY_MARKET_SCAN_CSV_HEADER_1;

@Component
public class DailyMarketScanReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyMarketScanReporter.class);

    private final String outputDirName = ApCo.REPORTS_DIR_NAME_DAILY_MARKET_SCAN;

    private final CalcRsiRepo calcRsiRepository;
    private final CalcMfiRepo calcMfiRepository;
    private final CalcAvgRepo calcAvgRepository;
    private final EmailService emailService;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;

    private final DataServiceI dataService;
    private final DateService dateService;

    DailyMarketScanReporter(CalcRsiRepo calcRsiRepository,
                            CalcMfiRepo calcMfiRepository,
                            CalcAvgRepo calcAvgRepository,
                            EmailService emailService,
                            NseFileUtils nseFileUtils,
                            PraFileUtils praFileUtils,
                            DataServiceI dataService, DateService dateService) {
        this.calcRsiRepository = calcRsiRepository;
        this.calcMfiRepository = calcMfiRepository;
        this.calcAvgRepository = calcAvgRepository;
        this.emailService = emailService;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.dataService = dataService;
        this.dateService = dateService;
        DirUtils.ensureFolder(outputDirName);
    }

    public void reportFromLast() {
//        String str = praFileUtils.validateDownload();
//        if(str == null) return;
//
//        LocalDate forDate = DateUtils.toLocalDate(str);
//        //LocalDate forDate = LocalDate.of(2020,1,3);
//        reportForDate(forDate, 10);
        reportFromLast(10);
    }
    public void reportFromLast(int forMinusDays) {
        String str = praFileUtils.validateDownloadCD();
        if(str == null) return;

        LocalDate forDate = DateUtils.toLocalDate(str);
        //LocalDate forDate = LocalDate.of(2020,1,3);
        reportForDate(forDate, forMinusDays);
    }
    public void reportForDate(LocalDate forDate, Integer forMinusDays) {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCD();
        if(forDate.isAfter(latestNseDate)) return;

        String fixed_width_days_str = "_".concat(forMinusDays.toString());
        fixed_width_days_str = fixed_width_days_str.substring(fixed_width_days_str.length()-2, fixed_width_days_str.length());
        String report_name = DMS.replace("days", fixed_width_days_str);

        //String fileName = report_name + "-" + forDate.toString() + ApCo.REPORTS_FILE_EXT;
        String fileName = report_name + "-" + forDate.toString() + "" + ApCo.REPORTS_FILE_EXT;
//        String filePath = ApCo.ROOT_DIR + File.separator + outputDirName + File.separator + fileName;
        String filePath = ApCo.ROOT_DIR + File.separator + outputDirName;

        String fullFileNameWithFullPath = filePath + File.separator + fileName;
        if(ApCo.PPF_REPORT_INCREMENTAL_FLAG) {
            for(char i='a'; i<'z'; i++) {
                if(nseFileUtils.isFilePresent(fullFileNameWithFullPath)) {
                    String fileName_IncrementRemoved = fileName.replace("-" + (char)(i-1) , "");
                    String fileName_NextIncrementAdded = fileName_IncrementRemoved.replace(".csv", "") + "-" + i + ".csv";
                    fileName = fileName_NextIncrementAdded;
                    fullFileNameWithFullPath = filePath + File.separator + fileName;

                } else {
                    break;
                }
            }
        }
        LOGGER.info("{} | for:{}", report_name, forDate.toString());
        if(nseFileUtils.isFilePresent(fullFileNameWithFullPath)) {
            LOGGER.warn("{} already present (regeneration and email would be skipped): {}", report_name, fullFileNameWithFullPath);
            return;
        }

        Map<String, List<DeliverySpikeDto>> symbolMap;
        symbolMap = prepareReportData(forDate, forMinusDays, fullFileNameWithFullPath);
        writeReport(fullFileNameWithFullPath, symbolMap);
        String str = "DMS-" +forDate+ "-(" +forMinusDays+ ").csv";
        emailReport(null, str, str, fullFileNameWithFullPath);
    }

    private Map<String, List<DeliverySpikeDto>> prepareReportData(LocalDate forDate, int forMinusDays, String filePath) {
        LocalDate minDate = dateService.getMinTradeDate(forDate, forMinusDays);

        //load Rsi
//        loadRsi(forDate, forMinusDays);
        //load Mfi
        loadMfi(forDate, forMinusDays);

        // load avg
//        List<CalcAvgTab> oldAvgListAll = calcAvgRepository.findAll();
        List<CalcAvgTab> oldAvgList = calcAvgRepository.findByTradeDateAndForDays(minDate, forMinusDays);
        Map<String, CalcAvgTab> calcAvgMap = oldAvgList.stream().collect(Collectors.toMap(row->row.getSymbol(), row-> row));

        Map<String, List<DeliverySpikeDto>> symbolMap;
        if(ApCo.ALL_FM_STOCKS) {
            symbolMap = dataService.getRichDataBySymbol(forDate, forMinusDays);
        } else {
            Set<String> symbolSet = LotSizeService.getSymbolSet(FmCategoryEnum.FM_TOP_70);
            symbolMap = dataService.getRichDataBySymbolForSymbolSet(forDate, forMinusDays, symbolSet);
        }

        ReportHelper.enrichDayTrend(symbolMap);
        ReportHelper.enrichGrowth(calcAvgMap, symbolMap);
        ReportHelper.enrichAtpDelAndOiTrend(symbolMap);
        ReportHelper.enrichNarrowRange(symbolMap);
        ReportHelper.enrichHammer(symbolMap);
        ReportHelper.enrichVwap(symbolMap);
        ReportHelper.enrichDeliveryDiversion(symbolMap);
        ReportHelper.enrichLongShortBuildup(symbolMap);
        //atpDelTrend


        // lot size
//        Map<String, List<DeliverySpikeDto>> tdySymbolMap = dataService.getRichDataBySymbol(forDate, 1);
//        List<DeliverySpikeDto> tdyDtos = symbolMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
//        tdyDtos.forEach( dto -> {
//            long lotSize = RefData.getLotSize(dto.getSymbol());
//            dto.setLotSize(lotSize);
//            if( null != dto.getFuTotTrdVal() && lotSize != 0) {
//                BigDecimal lotSiz = new BigDecimal(lotSize);
//                BigDecimal totalTradedQuantityOfFutures = dto.getFuContracts().multiply(lotSiz);
//                dto.setFuVol(totalTradedQuantityOfFutures);
//                BigDecimal totalTradeValueOfFutures = dto.getFuTotTrdVal();
//                BigDecimal fuAtp = NumberUtils.divide(totalTradeValueOfFutures, totalTradedQuantityOfFutures);
//                dto.setFuAtp(fuAtp);
//                BigDecimal fuAtpMinusCmAtp = fuAtp.subtract(dto.getAtp());
//                dto.setFuAtpMinusCmAtp(fuAtpMinusCmAtp);
////                BigDecimal fuOiLots = NumberUtils.divide(dto.getFuOi(), lotSiz);
////                dto.setFuOiLots(fuOiLots);
//            } else {
//                LOGGER.warn("{} - lot size not found in refDate", dto.getSymbol());
//            }
//                });
        return symbolMap;
    }

    private void loadRsi(LocalDate forDate, int forMinusDays) {
        LocalDate minDate = dateService.getMinTradeDate(forDate, forMinusDays+1);

        List<CalcRsiTab> oldRsiListOrdered = calcRsiRepository.findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(forMinusDays, minDate, forDate);
        Map<LocalDate, Map<String, CalcRsiTab>> tradeDateAndSymbolWise_RsiDoubleMap;
        tradeDateAndSymbolWise_RsiDoubleMap = ReportUtils.transformRsiInto_TradeDateAndSymbol_DoubleMap(oldRsiListOrdered);

        //List<CalcRsiTabNew> oldRsiListForRange = calcRsiRepository.findByForDaysAndTradeDateIsBetween(forMinusDays, minDate, forDate);
        //List<CalcRsiTabNew> oldRsiListForDate = calcRsiRepository.findByTradeDateAndForDays(forDate, forMinusDays);

        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolWise_DoubleMap;
        tradeDateAndSymbolWise_DoubleMap = dataService.getRichDataByTradeDateAndSymbol(forDate, forMinusDays+1);

        DeliverySpikeDto tdyDto = null;
        DeliverySpikeDto bckDto = null;
        BigDecimal chg = null;
        for(CalcRsiTab oldRsi:oldRsiListOrdered) {
            if(tradeDateAndSymbolWise_DoubleMap.containsKey(oldRsi.getTradeDate())) {
                if(tradeDateAndSymbolWise_DoubleMap.get(oldRsi.getTradeDate()).containsKey(oldRsi.getSymbol())) {
                    tdyDto = tradeDateAndSymbolWise_DoubleMap.get(oldRsi.getTradeDate()).get(oldRsi.getSymbol());
                    tdyDto.setCloseRsi(oldRsi.getCloseRsiSma());
                    tdyDto.setLastRsi(oldRsi.getLastRsiSma());
                    tdyDto.setAtpRsi(oldRsi.getAtpRsiSma());
                    tdyDto.setDelRsi(oldRsi.getDelRsiSma());
                    // skipt the first as it is to fetch back data only
                    if (bckDto != null) {
                        if(tdyDto.getBackDate().equals(bckDto.getTradeDate()) && tdyDto.getSymbol().equals(bckDto.getSymbol())) {
                            chg = tdyDto.getAtpRsi().subtract(bckDto.getAtpRsi());
                            tdyDto.setAtpRsiChg(chg);
                            chg = tdyDto.getDelRsi().subtract(bckDto.getDelRsi());
                            tdyDto.setDelRsiChg(chg);
                        }
                    }
                    bckDto = tdyDto;
                } else {
                    //LOGGER.warn("old rsi | symbol {} not found for tradeDate {}", oldRsi.getSymbol(), oldRsi.getTradeDate());
                }
            } else {
                //LOGGER.warn("old rsi | tradeDate {} not found for symbol {}", oldRsi.getTradeDate(), oldRsi.getSymbol());
            }
        }
    }

    private void loadMfi(LocalDate forDate, int forMinusDays) {
        LocalDate minDate = dateService.getMinTradeDate(forDate, forMinusDays+1);
        List<CalcMfiTab> oldMfiListOrdered = calcMfiRepository.findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(forMinusDays, minDate, forDate);
        Map<LocalDate, Map<String, CalcMfiTab>> tradeDateAndSymbolWise_MfiDoubleMap;
        tradeDateAndSymbolWise_MfiDoubleMap = ReportUtils.transformMfiInto_TradeDateAndSymbol_DoubleMap(oldMfiListOrdered);

        //List<CalcMfiTabNew> oldMfiListForRange = calcMfiRepository.findByForDaysAndTradeDateIsBetween(forMinusDays, minDate, forDate);
        //List<CalcMfiTabNew> oldMfiListForDate = calcMfiRepository.findByTradeDateAndForDays(forDate, forMinusDays);
        //ReportHelper.enrichMfi(oldMfiList, tradeDateAndSymbolWise_DoubleMap);

        Map<LocalDate, Map<String, DeliverySpikeDto>> tradeDateAndSymbolWise_DoubleMap;
        tradeDateAndSymbolWise_DoubleMap = dataService.getRichDataByTradeDateAndSymbol(forDate, forMinusDays+1);

        DeliverySpikeDto tdyDto = null;
        DeliverySpikeDto bckDto = null;
        BigDecimal chg = null;
        for(CalcMfiTab oldMfi:oldMfiListOrdered) {
            if(tradeDateAndSymbolWise_DoubleMap.containsKey(oldMfi.getTradeDate())) {
                if(tradeDateAndSymbolWise_DoubleMap.get(oldMfi.getTradeDate()).containsKey(oldMfi.getSymbol())) {
                    tdyDto = tradeDateAndSymbolWise_DoubleMap.get(oldMfi.getTradeDate()).get(oldMfi.getSymbol());
                    tdyDto.setVolAtpMfi(oldMfi.getVolAtpMfiSma());
                    tdyDto.setDelAtpMfi(oldMfi.getDelAtpMfiSma());
                    // skipt the first as it is to fetch back data only
                    if (bckDto != null) {
                        if(tdyDto.getBackDate().equals(bckDto.getTradeDate()) && tdyDto.getSymbol().equals(bckDto.getSymbol())) {
                            chg = tdyDto.getDelAtpMfi().subtract(bckDto.getDelAtpMfi());
                            tdyDto.setDelAtpMfiChg(chg);
                        }
                    }
                    bckDto = tdyDto;
                } else {
                    //LOGGER.warn("old rsi | symbol {} not found for tradeDate {}", oldRsi.getSymbol(), oldRsi.getTradeDate());
                }
            } else {
                //LOGGER.warn("old rsi | tradeDate {} not found for symbol {}", oldRsi.getTradeDate(), oldRsi.getSymbol());
            }
        }
    }

    private boolean filterDate(CalcAvgTab pojo, LocalDate minDate, LocalDate maxDate) {
        return pojo.getTradeDate().isAfter(minDate.minusDays(1)) && pojo.getTradeDate().isBefore(maxDate.plusDays(1));
    }

    private void writeReport(String toPath, Map<String, List<DeliverySpikeDto>> symbolMap) {
        List<String> keys = symbolMap.keySet().stream().collect(Collectors.toList());
        Collections.sort(keys);

        List<DeliverySpikeDto> lists = new LinkedList<>();
        keys.forEach( key -> lists.addAll(symbolMap.get(key)) );
        writeReport(toPath, lists);
    }
    private void writeReport(String toPath, List<DeliverySpikeDto> dtos) {
        if(dtos == null || dtos.size() == 0 || dtos.isEmpty()) {
            LOGGER.warn("no data to create report");
            return;
        }
        // create and collect csv lines
        List<String> csvLines = new LinkedList<>();
        dtos.forEach( dto -> csvLines.add(dto.toDailyMarketScanString()) );

        // print csv lines
        File csvOutputFile = new File(toPath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(DAILY_MARKET_SCAN_CSV_HEADER_1);
            csvLines.stream()
                    //.map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error:", e);
            throw new RuntimeException(DMS + ": Could not create file");
        }
    }

    private void emailReport(String toEmail, String subject, String text, String pathToAttachment) {
        if(nseFileUtils.isFilePresent(pathToAttachment)) {
            emailService.sendAttachmentMessage("pradeepjindal.mca@gmail.com", subject, text, pathToAttachment, null);
        } else {
            LOGGER.error("skipping email: DeliverySpikeReport not found at disk");
        }
    }

}
