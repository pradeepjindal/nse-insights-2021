package org.pra.nse.processor;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.ProCo;
import org.pra.nse.csv.bean.out.PraBean;
import org.pra.nse.csv.merge.CmMerger;
import org.pra.nse.csv.merge.DmMerger;
import org.pra.nse.csv.merge.FmMerger;
import org.pra.nse.csv.writer.ManishCsvWriter;
import org.pra.nse.email.EmailService;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


//@Component
public class ShuviProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShuviProcessor.class);

    private final String File_Name = ApCo.SHUVI_FILE_NAME;
    private final CmMerger cmMerger;
    private final FmMerger fmMerger;
    private final DmMerger dmMerger;
    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;
    private final ManishCsvWriter csvWriter;
    private final EmailService emailService;

    public ShuviProcessor(CmMerger cmMerger, FmMerger fmMerger, DmMerger dmMerger,
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
        String outputPathAndFileNameForFixFile = ProCo.outputPathAndFileNameForFixFile(File_Name);
        String foLatestFileName = praFileUtils.getLatestFileNameFor(NseCons.FM_FILES_PATH, PraCons.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1, processForDate);
        String outputPathAndFileNameForDynamicFile = ProCo.outputPathAndFileNameForDynamicFile(File_Name, foLatestFileName);

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

        if(nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile)) {
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
            LOGGER.info("SUCCESS! {} has been placed at: {}", File_Name, outputPathAndFileNameForDynamicFile);
            LOGGER.info("---------------------------------------------------------------------------------------------------------------");
        } else {
            LOGGER.info("ERROR! {} something got wrong, could not create data file.", File_Name);
        }

        //email
        LocalDate fileDate = DateUtils.toLocalDate(ProCo.extractDate(foLatestFileName));
        if(nseFileUtils.isFilePresent(outputPathAndFileNameForDynamicFile) && fileDate.compareTo(ApCo.EMAIL_FROM_DATE) > -1) {
            String fileName = File_Name +"-"+ ProCo.extractDate(foLatestFileName) + ApCo.REPORTS_FILE_EXT;
            emailService.sendAttachmentMessage("ca.manish.thakkar@gmail.com", fileName, fileName, outputPathAndFileNameForDynamicFile, File_Name);
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
