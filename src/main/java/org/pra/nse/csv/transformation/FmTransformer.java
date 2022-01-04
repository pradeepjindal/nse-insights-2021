package org.pra.nse.csv.transformation;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FmTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmTransformer.class);

    private final String sourceDirName = NseCons.FM_DIR_NAME;
    private final String sourceFilePrefix = NseCons.NSE_FM_FILE_PREFIX;
    private final String sourceFileSuffix = NseCons.NSE_FM_FILE_SUFFIX;
    private final String sourceFileExtension = ApCo.ZIP_FILE_EXT;

    private final String targetDirName = PraCons.FM_DIR_NAME;
    private final String targetFilePrefix = PraCons.PRA_FM_FILE_PREFIX;
    private final String targetFileExtension = ApCo.CSV_FILE_EXT;

    private final LocalDate defaultDate = ApCo.TRANSFORM_NSE_FROM_DATE;

    private final String Source_Data_Dir = ApCo.ROOT_DIR + File.separator + sourceDirName;
    private final String Target_Data_Dir = ApCo.ROOT_DIR + File.separator + targetDirName;


    public FmTransformer(TransformationHelper transformationHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(transformationHelper, nseFileUtils, praFileUtils);
    }


    public void transformFromDefaultDate() {
        transformFromDate(defaultDate);
    }
    public void transformFromDate(LocalDate fromDate) {
        Map<String, String> filePairMap = prepare(fromDate);
        looper(filePairMap);
    }

    public void transformFromLatestDate() {
        LocalDate dateOfLatestFile;
        Map<String, String> filePairMap;
        String latestFileName = praFileUtils.getLatestFileNameFor(Target_Data_Dir, targetFilePrefix, targetFileExtension, 1);
        if(latestFileName == null)
            dateOfLatestFile = defaultDate;
        else
            dateOfLatestFile = DateUtils.getLocalDateFromPath(latestFileName);
        filePairMap = prepare(dateOfLatestFile);
        //TODO filter the existing files
        looper(filePairMap);
    }

    private Map<String, String> prepare(LocalDate fromDate) {
        List<String> sourceFileNames = nseFileUtils.constructFileNames(
                            fromDate, NseCons.NSE_FM_FILE_NAME_DATE_FORMAT, sourceFilePrefix, sourceFileSuffix + sourceFileExtension);
        //filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        //
        Map<String, String> filePairMap = new LinkedHashMap<>();
//        sourceFileNames.forEach( sourceFileName -> {
//            LOGGER.info("{}", sourceFileName);
//            //DateUtils.extractDate(fileName, ApCo.NSE_FM_FILE_NAME_DATE_REGEX, ApCo.NSE_FM_FILE_NAME_DATE_FORMAT);
//            LocalDate localDate = DateUtils.getLocalDateFromPath(sourceFileName, NseCo.NSE_FM_FILE_NAME_DATE_REGEX, NseCo.NSE_FM_FILE_NAME_DATE_FORMAT);
//            String targetFileName = ApCo.PRA_FM_FILE_PREFIX + localDate.toString() + ApCo.REPORTS_FILE_EXT;
//            filePairMap.put(sourceFileName, targetFileName);
//        });
        filePairMap = TransformationHelper.prepareFileNames(sourceFileNames,
                NseCons.NSE_FM_FILE_NAME_DATE_REGEX, NseCons.NSE_FM_FILE_NAME_DATE_FORMAT,
                targetFilePrefix, targetFileExtension, ApCo.DATA_FILE_NAME_DTF);
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        //TODO - block transforming of 28-Aug-2019 file
        filePairMap.forEach(this::validateAndTransform);
    }

    private void validateAndTransform(String sourceFileName, String targetFileName) {
        String source = Source_Data_Dir + File.separator + sourceFileName;
        String target = Target_Data_Dir + File.separator + targetFileName;

        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("FM | already transformed - {}", target);
            return;
        }

        if (nseFileUtils.isFileAbsent(source)) {
            LOGGER.info("FM | source not found - {}", source);
            return;
        }

        long bytes = 0;
        try {
            bytes = Files.size(Paths.get(source));
        } catch (IOException e) {
            LOGGER.error("FM | error reading file - {}", source);
        }

        if (bytes == 0) {
            LOGGER.warn("FM | file size is ZERO (may be holiday file) - {}", source);
            return;
        }

        try {
            transformationHelper.transform(Source_Data_Dir, Target_Data_Dir, PraCons.PRA_FM_FILE_PREFIX, sourceFileName, targetFileName);
            LOGGER.info("FM | transformed");
        } catch (Exception e) {
            LOGGER.warn("FM | Error while transforming file: {} {}", source, e);
        }
    }


}
