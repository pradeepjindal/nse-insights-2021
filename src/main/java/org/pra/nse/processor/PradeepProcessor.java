package org.pra.nse.processor;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.ProCo;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.merge.CmMerger;
import org.pra.nse.csv.merge.FmMerger;
import org.pra.nse.csv.merge.DmMerger;
import org.pra.nse.csv.writer.PradeepCsvWriter;
import org.pra.nse.util.PraFileUtils;
import org.pra.nse.util.NseFileUtils;
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
public class PradeepProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PradeepProcessor.class);

    private final CmMerger cmMerger;
    private final FmMerger fmMerger;
    private final DmMerger dmMerger;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final PradeepCsvWriter csvWriter;

    public PradeepProcessor(CmMerger cmMerger, FmMerger fmMerger, DmMerger dmMerger,
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

    public void process(LocalDate forDate) throws IOException {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCDF();
        if(forDate.isAfter(latestNseDate)) return;

        String outputPathAndFileNameForFixFile = ProCo.outputPathAndFileNameForFixFile(ApCo.PRADEEP_FILE_NAME);
        String foLatestFileName = praFileUtils.getLatestFileNameFor(NseCons.FM_FILES_PATH, ApCo.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1, forDate);
        String outputPathAndFileNameForDynamicFile = ProCo.outputPathAndFileNameForDynamicFile(ApCo.PRADEEP_FILE_NAME, foLatestFileName);

        if(nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.warn("report already present (regeneration and email would be skipped): {}", outputPathAndFileNameForDynamicFile);
            return;
        }

        List<PraBean> praBeans = new ArrayList<>();
        TreeSet<LocalDate> foMonthlyExpiryDates = fmMerger.readAndMerge(praBeans, forDate);
        dmMerger.readAndMerge(praBeans, forDate);
        cmMerger.readAndMerge(praBeans, forDate);

        //-------------------------------------------------------
        csvWriter.write(praBeans, outputPathAndFileNameForFixFile, foMonthlyExpiryDates);

        //-------------------------------------------------------
        csvWriter.write(praBeans, outputPathAndFileNameForDynamicFile, foMonthlyExpiryDates);

        //-------------------------------------------------------
        if(nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
            LOGGER.info("SUCCESS! praData.csv File has been placed at: " + outputPathAndFileNameForDynamicFile);
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
        } else {
            LOGGER.info("ERROR! something got wrong, could not create data file.");
        }
    }

    //@Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Pradeep Processor | ============================== | Kicking");
		try {
            process (LocalDate.now());
		} catch(Exception e) {
            LOGGER.error("error:", e);
		}
        LOGGER.info("Pradeep Processor | ============================== | Finished");
    }
}
