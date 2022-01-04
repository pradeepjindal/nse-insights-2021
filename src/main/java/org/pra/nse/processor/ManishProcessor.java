package org.pra.nse.processor;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.ProCo;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.merge.CmMerger;
import org.pra.nse.csv.merge.FmMerger;
import org.pra.nse.csv.merge.DmMerger;
import org.pra.nse.csv.writer.ManishCsvWriter;
import org.pra.nse.email.EmailService;
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
public class ManishProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManishProcessor.class);

    private final CmMerger cmMerger;
    private final FmMerger fmMerger;
    private final DmMerger dmMerger;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final ManishCsvWriter csvWriter;
    private final EmailService emailService;

    public ManishProcessor(CmMerger cmMerger, FmMerger fmMerger, DmMerger dmMerger,
                           ManishCsvWriter csvWriter,
                           NseFileUtils nseFileUtils,
                           PraFileUtils praFileUtils,
                           EmailService emailService) {
        this.cmMerger = cmMerger;
        this.fmMerger = fmMerger;
        this.dmMerger = dmMerger;
        this.csvWriter = csvWriter;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.emailService = emailService;
    }

    public void process(LocalDate processForDate) throws IOException {
        String outputPathAndFileNameForFixFile = ProCo.outputPathAndFileNameForFixFile(ApCo.MANISH_FILE_NAME);
        String foLatestFileName = praFileUtils.getLatestFileNameFor(PraCons.FM_FILES_PATH, PraCons.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1, processForDate);
        String outputPathAndFileNameForDynamicFile = ProCo.outputPathAndFileNameForDynamicFile(ApCo.MANISH_FILE_NAME, foLatestFileName);

        if(nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.warn("report already present (regeneration and email would be skipped): {}", outputPathAndFileNameForDynamicFile);
            return;
        }

        List<PraBean> praBeans = new ArrayList<>();
        TreeSet<LocalDate> foMonthlyExpiryDates = fmMerger.readAndMerge(praBeans, processForDate);
        dmMerger.readAndMerge(praBeans, processForDate);
        cmMerger.readAndMerge(praBeans, processForDate);
        //-------------------------------------------------------
        LOGGER.info("Fix File:{}", outputPathAndFileNameForFixFile);
        csvWriter.write(praBeans, outputPathAndFileNameForFixFile, foMonthlyExpiryDates);
        //-------------------------------------------------------
        LOGGER.info("Dynamic File:{}", outputPathAndFileNameForDynamicFile);
        csvWriter.write(praBeans, outputPathAndFileNameForDynamicFile, foMonthlyExpiryDates);
        //-------------------------------------------------------

        if (nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
            LOGGER.info("SUCCESS! manishData.csv File has been placed at: " + outputPathAndFileNameForDynamicFile);
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
        } else {
            LOGGER.info("ERROR! something got wrong, could not create data file.");
        }

        //email
        if ( nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)  ) {
            String fileName = ApCo.MANISH_FILE_NAME +"-"+ ProCo.extractDate(foLatestFileName) + ApCo.REPORTS_FILE_EXT;
            emailService.sendAttachmentMessage("ca.manish.thakkar@gmail.com", fileName, fileName, outputPathAndFileNameForDynamicFile, ApCo.MANISH_FILE_NAME_B);
        }
    }

    //@Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Manish Processor | ============================== | Kicking");
		try {
            process(LocalDate.now());
		} catch(Exception e) {
            LOGGER.error("error:", e);
		}
        LOGGER.info("Manish Processor | ============================== | Finished");
    }
}
