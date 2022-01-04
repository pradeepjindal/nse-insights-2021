package org.pra.nse.processor;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.ProCo;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.merge.CmMerger;
import org.pra.nse.csv.merge.FmMerger;
import org.pra.nse.csv.merge.DmMerger;
import org.pra.nse.csv.writer.ManishCsvWriterB;
import org.pra.nse.email.EmailService;
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
public class ManishProcessorB {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManishProcessorB.class);

    private final CmMerger cmMerger;
    private final FmMerger fmMerger;
    private final DmMerger dmMerger;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final ManishCsvWriterB csvWriterB;
    private final EmailService emailService;

    public ManishProcessorB(CmMerger cmMerger, FmMerger fmMerger, DmMerger dmMerger,
                            ManishCsvWriterB csvWriterB,
                            NseFileUtils nseFileUtils,
                            PraFileUtils praFileUtils,
                            EmailService emailService) {
        this.cmMerger = cmMerger;
        this.fmMerger = fmMerger;
        this.dmMerger = dmMerger;
        this.csvWriterB = csvWriterB;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.emailService = emailService;
    }

    public void process(String outputFileName, LocalDate forDate) throws IOException {
        LocalDate latestNseDate = praFileUtils.getLatestNseDateCDF();
        if(forDate.isAfter(latestNseDate)) return;

        String outputPathAndFileNameForFixFile = ProCo.outputPathAndFileNameForFixFile(outputFileName);
        String foLatestFileName = praFileUtils.getLatestFileNameFor(PraCons.FM_FILES_PATH, PraCons.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1, forDate);
        String outputPathAndFileNameForDynamicFile = ProCo.outputPathAndFileNameForDynamicFile(outputFileName, foLatestFileName);

        if(nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.warn("report already present (regeneration and email would be skipped): {}", outputPathAndFileNameForDynamicFile);
            return;
        }

        List<PraBean> praBeans = new ArrayList<>();
        TreeSet<LocalDate> foMonthlyExpiryDates = fmMerger.readAndMerge(praBeans, forDate);
        dmMerger.readAndMerge(praBeans, forDate);
        cmMerger.readAndMerge(praBeans, forDate);

        //-------------------------------------------------------
        LOGGER.info("Fix File:{}", outputPathAndFileNameForFixFile);
        csvWriterB.write(praBeans, outputPathAndFileNameForFixFile, foMonthlyExpiryDates);

        //-------------------------------------------------------
        LOGGER.info("Dynamic File:{}", outputPathAndFileNameForDynamicFile);
        csvWriterB.write(praBeans, outputPathAndFileNameForDynamicFile, foMonthlyExpiryDates);

        //-------------------------------------------------------
        if (nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
            LOGGER.info("SUCCESS! manishDataB.csv File has been placed at: " + outputPathAndFileNameForDynamicFile);
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
        } else {
            LOGGER.info("ERROR! something got wrong, could not create data file.");
        }

        // email
        if ( nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile) ) {
            String fileName = ApCo.MANISH_FILE_NAME_B +"-"+ ProCo.extractDate(foLatestFileName) + ApCo.REPORTS_FILE_EXT;
            emailService.sendAttachmentMessage("ca.manish.thakkar@gmail.com", fileName, fileName, outputPathAndFileNameForDynamicFile, ApCo.MANISH_FILE_NAME_B);
            emailService.sendAttachmentMessage("pradeepjindal.mca@gmail.com", fileName, fileName, outputPathAndFileNameForDynamicFile, ApCo.MANISH_FILE_NAME_B);
        }
    }

    //@Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Manish Processor B | ============================== | Kicking");
		try {
            process(ApCo.MANISH_FILE_NAME_B, LocalDate.now());
		} catch(Exception e) {
            LOGGER.error("error:", e);
		}
        LOGGER.info("Manish Processor B | ============================== | Finished");
    }
}
