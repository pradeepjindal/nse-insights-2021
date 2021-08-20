package org.pra.nse.csv.transformation;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CmTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmTransformer .class);

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + NseCons.CM_DIR_NAME;
    private final String Target_Data_Dir = ApCo.ROOT_DIR + File.separator + "pra-cm";

    public CmTransformer(TransformationHelper transformationHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(transformationHelper, nseFileUtils, praFileUtils);
    }


    public void transformFromDefaultDate() {
        transformFromDate(ApCo.TRANSFORM_NSE_FROM_DATE);
    }
    public void transformFromDate(LocalDate fromDate) {
        Map<String, String> filePairMap = prepare(fromDate);
        looper(filePairMap);
    }

    public void transformFromLatestDate() {
        String str = praFileUtils.getLatestFileNameFor(Target_Data_Dir, ApCo.PRA_CM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str);
        Map<String, String> filePairMap = prepare(dateOfLatestFile);
        //TODO filter the existing files
        looper(filePairMap);
    }


    private Map<String, String> prepare(LocalDate fromDate) {
        List<String> sourceFileNames = nseFileUtils.constructFileNames(
                fromDate,
                NseCons.NSE_CM_FILE_NAME_DATE_FORMAT,
                NseCons.NSE_CM_FILE_PREFIX,
                NseCons.NSE_CM_FILE_SUFFIX + NseCons.NSE_CM_FILE_EXT);
        //filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        //
        Map<String, String> filePairMap = new LinkedHashMap<>();
//        sourceFileNames.forEach( sourceFileName -> {
//            LOGGER.info("{}", sourceFileName);
//            //DateUtils.extractDate(fileName, ApCo.NSE_CM_FILE_NAME_DATE_REGEX, ApCo.NSE_CM_FILE_NAME_DATE_FORMAT);
//            LocalDate localDate = DateUtils.getLocalDateFromPath(sourceFileName, NseCo.NSE_CM_FILE_NAME_DATE_REGEX, NseCo.NSE_CM_FILE_NAME_DATE_FORMAT);
//            String targetFileName = ApCo.PRA_CM_FILE_PREFIX + localDate.toString() + ApCo.REPORTS_FILE_EXT;
//            filePairMap.put(sourceFileName, targetFileName);
//        });
        filePairMap = TransformationHelper.prepareFileNames(sourceFileNames,
                NseCons.NSE_CM_FILE_NAME_DATE_REGEX, NseCons.NSE_CM_FILE_NAME_DATE_FORMAT,
                ApCo.PRA_CM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, ApCo.DATA_FILE_NAME_DTF);
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach(this::validateAndTransform);
    }

    private void validateAndTransform(String nseFileName, String praFileName) {
        String source = Data_Dir + File.separator + nseFileName;
        String target = Target_Data_Dir + File.separator + praFileName;

        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("CM | already transformed - {}", target);
            return;
        }

        if (nseFileUtils.isFileAbsent(source)) {
            LOGGER.info("CM | source not found - {}", source);
            return;
        }

        long fileSize = 0;
        try {
            fileSize = Files.size(Paths.get(source));
        } catch (IOException e) {
            LOGGER.error("CM - error reading file - {}", source);
        }

        if (fileSize == 0) {
            LOGGER.warn("CM file size is ZERO (may be holiday file) - {}", source);
            return;
        }

        try {
            transformationHelper.transform(Data_Dir, Target_Data_Dir, ApCo.PRA_CM_FILE_PREFIX, nseFileName, praFileName);
            LOGGER.info("CM | transformed");
        } catch (Exception e) {
            LOGGER.warn("CM | Error while transforming file: {} {}", source, e);
        }
    }

}
