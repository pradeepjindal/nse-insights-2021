package org.pra.nse.processor;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.ProCo;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.merge.CmMerger;
import org.pra.nse.csv.merge.FmMerger;
import org.pra.nse.csv.merge.DmMerger;
import org.pra.nse.csv.writer.PradeepCsvWriter;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


@Component
public class PradeepProcessorB {
    private static final Logger LOGGER = LoggerFactory.getLogger(PradeepProcessorB.class);

    private final CmMerger cmMerger;
    private final FmMerger fmMerger;
    private final DmMerger dmMerger;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final PradeepCsvWriter csvWriter;

    public PradeepProcessorB(CmMerger cmMerger, FmMerger fmMerger, DmMerger dmMerger,
                             PradeepCsvWriter csvWriter,
                             NseFileUtils nseFileUtils,
                             PraFileUtils praFileUtils) {
        this.cmMerger = cmMerger;
        this.fmMerger = fmMerger;
        this.dmMerger = dmMerger;
        this.csvWriter = csvWriter;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

    public void process(LocalDate processForDate) throws IOException {

        List<PraBean> praBeans = new ArrayList<>();
        // FO
        TreeSet<LocalDate> foMonthlyExpiryDates = fmMerger.readAndMerge(praBeans, processForDate);
        // MAT
        dmMerger.readAndMerge(praBeans, processForDate);
        // CM
        cmMerger.readAndMerge(praBeans, processForDate);

        //-------------------------------------------------------
        String outputPathAndFileNameForFixFile = ProCo.outputPathAndFileNameForFixFile(ApCo.PRADEEP_FILE_NAME, "-b");
        csvWriter.write(praBeans, outputPathAndFileNameForFixFile, foMonthlyExpiryDates);
        //-------------------------------------------------------
        //String foLatestFileName = praNameUtils.getLatestFileNameFor(ApCo.FO_FILES_PATH, ApCo.FO_DATA_FILE_PREFIX, ApCo.PRA_DATA_FILE_EXT, 1);
        String foLatestFileName = praFileUtils.getLatestFileNameFor(NseCons.FM_FILES_PATH, ApCo.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1, processForDate);
        String outputPathAndFileNameForDynamicFile = ProCo.outputPathAndFileNameForDynamicFile(ApCo.PRADEEP_FILE_NAME, foLatestFileName, "-b");
                csvWriter.write(praBeans, outputPathAndFileNameForDynamicFile, foMonthlyExpiryDates);
        //-------------------------------------------------------

        if(nseFileUtils.isFileExist(outputPathAndFileNameForDynamicFile)) {
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
            LOGGER.info("SUCCESS! praData.csv File has been placed at: " + outputPathAndFileNameForDynamicFile);
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
        } else {
            LOGGER.info("ERROR! something got wrong, could not create data file.");
        }
    }

    //@Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Pradeep Processor B | ============================== | Kicking");
		try {
            process(LocalDate.now());
		} catch(Exception e) {
            LOGGER.error("{}", e);
		}
        LOGGER.info("Pradeep Processor B | ============================== | Finished");
    }
}
