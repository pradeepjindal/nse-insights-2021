package org.pra.nse.csv.transformation;

import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransformationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationHelper.class);

    private final NseFileUtils nseFileUtils;

    public TransformationHelper(NseFileUtils nseFileUtils) {
        this.nseFileUtils = nseFileUtils;
    }

    public static Map<String, String> prepareFileNames(List<String> fileNames,
                                                       String sourceFileNameDateRegex, String sourceFileNameDateFormat,
                                                       String targetFilePrefix, String targetFileExt, DateTimeFormatter fileDtf) {
        Map<String, String> filePairMap = new LinkedHashMap<>();
        fileNames.forEach( sourceFileName -> {
            LOGGER.info("{}", sourceFileName);
            //DateUtils.extractDate(fileName, ApCo.NSE_CM_FILE_NAME_DATE_REGEX, ApCo.NSE_CM_FILE_NAME_DATE_FORMAT);
            LocalDate localDate = DateUtils.getLocalDateFromPath(sourceFileName, sourceFileNameDateRegex, sourceFileNameDateFormat);
            String targetFileName = targetFilePrefix + fileDtf.format(localDate) + targetFileExt;
            filePairMap.put(sourceFileName, targetFileName);
        });
        return filePairMap;
    }

    void transform(String dataDir, String filePrefix, String nseFileName, String praFileName) {
        String source = dataDir + File.separator + nseFileName;
        String target = dataDir + File.separator + praFileName;
        if(nseFileUtils.isFileExist(target)) {
            LOGGER.info("{} already transformed - {}", filePrefix, target);
        } else if (nseFileUtils.isFileExist(source)) {
            try {
                //TODO pass on the target file name
                nseFileUtils.unzip2(source, filePrefix);
                LOGGER.info("{} transformed - {}", filePrefix, target);
            } catch (FileNotFoundException fnfe) {
                LOGGER.info("{} file not found - {}", filePrefix, source);
            } catch (IOException e) {
                LOGGER.warn("Error while unzipping file: {}", e);
            }
        } else {
            LOGGER.error("source not found ({})", source);
        }
    }

    void transform(String srcDataDir, String tgtDataDir, String filePrefix, String nseFileName, String praFileName) {
        String source = srcDataDir + File.separator + nseFileName;
        String target = tgtDataDir + File.separator + praFileName;
        if(nseFileUtils.isFileExist(target)) {
            LOGGER.info("{} already transformed - {}", filePrefix, target);
        } else if (nseFileUtils.isFileExist(source)) {
            try {
                //TODO pass on the target file name
                nseFileUtils.unzip2(source, tgtDataDir, filePrefix);
                LOGGER.info("{} transformed - {}", filePrefix, target);
            } catch (FileNotFoundException fnfe) {
                LOGGER.info("{} file not found - {}", filePrefix, source);
            } catch (IOException e) {
                LOGGER.warn("Error while unzipping file: {}", e);
            }
        } else {
            LOGGER.error("source not found ({})", source);
        }
    }

}
