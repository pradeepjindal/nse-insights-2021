package org.pra.nse;

import org.pra.nse.calculation.CalculationManager;
import org.pra.nse.csv.download.DownloadManager;
import org.pra.nse.csv.transformation.TransformationManager;
import org.pra.nse.db.upload.CalcUploadManager;
import org.pra.nse.db.upload.NseUploadManager;
import org.pra.nse.processor.*;
import org.pra.nse.refdata.RefData;
import org.pra.nse.report.ReportManagerNew;
import org.pra.nse.statistics.StatisticsManager;
import org.pra.nse.util.DirUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class MainProcess implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainProcess.class);

    private final PraFileUtils praFileUtils;

    private final DownloadManager downloadManager;
    private final TransformationManager transformationManager;
    private final NseUploadManager nseUploadManager;
    private final CalculationManager calculationManager;
    private final CalcUploadManager calcUploadManager;
    private final ProcessManager processManager;
    private final ReportManagerNew reportManagerNew;
    private final StatisticsManager statisticsManager;

    public MainProcess(PraFileUtils praFileUtils,
                       DownloadManager downloadManager,
                       TransformationManager transformationManager,
                       NseUploadManager nseUploadManager,
                       CalculationManager calculationManager,
                       CalcUploadManager calcUploadManager, ProcessManager processManager,
                       ReportManagerNew reportManagerNew, StatisticsManager statisticsManager) {
        this.praFileUtils = praFileUtils;
        this.downloadManager = downloadManager;
        this.transformationManager = transformationManager;
        this.nseUploadManager = nseUploadManager;
        this.calculationManager = calculationManager;
        this.calcUploadManager = calcUploadManager;
        this.processManager = processManager;
        this.reportManagerNew = reportManagerNew;
        this.statisticsManager = statisticsManager;
    }

    @Override
    public void run(ApplicationArguments args) {
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
                reportManagerNew.execute();
            }

//            if(praFileUtils.validateDownloadCDF() != null) {
//                processManager.execute();
//                //statisticsManager.execute();
//            }

            //statisticsManager.execute();

        } catch(Exception e) {
            LOGGER.error("ERROR: {}", e);
        }
        LOGGER.info("");
        LOGGER.info("Main Process | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ | finishing");
        LOGGER.info("");
    }

}
