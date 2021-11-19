package org.pra.nse;

import org.pra.nse.calculation.CalculationManager;
import org.pra.nse.calculation.RsiCalcLib;
import org.pra.nse.csv.download.DownloadManager;
import org.pra.nse.csv.transformation.TransformationManager;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.db.upload.CalcUploadManager;
import org.pra.nse.db.upload.NseUploadManager;
import org.pra.nse.processor.*;
import org.pra.nse.report.ReportManager;
import org.pra.nse.service.DataServiceI;
import org.pra.nse.statistics.StatisticsManager;
import org.pra.nse.util.DirUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class MainProcess implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainProcess.class);

    private final DataServiceI dataService;
    private final PraFileUtils praFileUtils;

    private final DownloadManager downloadManager;
    private final TransformationManager transformationManager;
    private final NseUploadManager nseUploadManager;
    private final CalculationManager calculationManager;
    private final CalcUploadManager calcUploadManager;
    private final ProcessManager processManager;
    private final ReportManager reportManager;
    private final StatisticsManager statisticsManager;

    public MainProcess(DataServiceI dataService,
                       PraFileUtils praFileUtils,
                       DownloadManager downloadManager,
                       TransformationManager transformationManager,
                       NseUploadManager nseUploadManager,
                       CalculationManager calculationManager,
                       CalcUploadManager calcUploadManager, ProcessManager processManager,
                       ReportManager reportManager, StatisticsManager statisticsManager) {
        this.dataService = dataService;
        this.praFileUtils = praFileUtils;
        this.downloadManager = downloadManager;
        this.transformationManager = transformationManager;
        this.nseUploadManager = nseUploadManager;
        this.calculationManager = calculationManager;
        this.calcUploadManager = calcUploadManager;
        this.processManager = processManager;
        this.reportManager = reportManager;
        this.statisticsManager = statisticsManager;
    }

    @Override
    public void run(ApplicationArguments args) {

//        String forSymbol = "HDFC";
//        LocalDate forDate = LocalDate.of(2021, 9, 17);
//        int forDays = 14;
//        Map<String, List<DeliverySpikeDto>> symbolMap = null;
//        symbolMap = dataService.getRawDataBySymbol( forDate, forDays, forSymbol);
//        List<DeliverySpikeDto> mapDtoList_OfGivenSymbol = symbolMap.get(forSymbol);
//        RsiCalcLib.calculateRsiOnClose(dataService, forDate, forDays, forSymbol, mapDtoList_OfGivenSymbol);
//        RsiCalcLib.calculateRsiOnLast(dataService, forDate, forDays, forSymbol, mapDtoList_OfGivenSymbol);


        LOGGER.info("");
        LOGGER.info("Main Process | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ | commencing");
        LOGGER.info("");

        try {
            DirUtils.createRootFolder();
            downloadManager.execute();
            transformationManager.execute();
            nseUploadManager.execute();

            if(praFileUtils.validateDownloadCD() != null) {
                calculationManager.execute();
                calcUploadManager.execute();
                reportManager.execute();
            }

            if(praFileUtils.validateDownloadCDF() != null) {
                processManager.execute();
                //statisticsManager.execute();
            }

            //statisticsManager.execute();

        } catch(Exception e) {
            LOGGER.error("ERROR:", e);
        }

        LOGGER.info("");
        LOGGER.info("Main Process | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ | finishing");
        LOGGER.info("");
    }

}
