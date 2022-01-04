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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class DmTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmTransformer.class);

    private final String sourceDirName = NseCons.DM_DIR_NAME;
    private final String sourceFilePrefix = NseCons.NSE_DM_FILE_PREFIX;
    private final String sourceFileExtension = NseCons.NSE_DM_FILE_EXT;

    private final String targetDirName = PraCons.DM_DIR_NAME;
    private final String targetFilePrefix = PraCons.PRA_DM_FILE_PREFIX;
    private final String targetFileExtension = ApCo.CSV_FILE_EXT;

    private final LocalDate defaultDate = ApCo.TRANSFORM_NSE_FROM_DATE;

    private final String Source_Data_Dir = ApCo.ROOT_DIR + File.separator + sourceDirName;
    private final String Target_Data_Dir = ApCo.ROOT_DIR + File.separator + targetDirName;


    public DmTransformer(TransformationHelper transformationHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
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
                fromDate,
                NseCons.NSE_DM_FILE_NAME_DATE_FORMAT,
                sourceFilePrefix,
                sourceFileExtension);
        //filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        //
        Map<String, String> filePairMap = new LinkedHashMap<>();
//        fileNames.forEach( sourceFileName -> {
//            LOGGER.info("{}", sourceFileName);
//            //DateUtils.extractDate(fileName, ApCo.NSE_DM_FILE_NAME_DATE_REGEX, ApCo.NSE_DM_FILE_NAME_DATE_FORMAT);
//            LocalDate localDate = DateUtils.getLocalDateFromPath(sourceFileName, NseCo.NSE_DM_FILE_NAME_DATE_REGEX, NseCo.NSE_DM_FILE_NAME_DATE_FORMAT);
//            String targetFileName = ApCo.PRA_DM_FILE_PREFIX + localDate.toString() + ApCo.REPORTS_FILE_EXT;
//            filePairMap.put(sourceFileName, targetFileName);
//        });
        filePairMap = TransformationHelper.prepareFileNames(sourceFileNames,
                NseCons.NSE_DM_FILE_NAME_DATE_REGEX, NseCons.NSE_DM_FILE_NAME_DATE_FORMAT,
                targetFilePrefix, targetFileExtension, ApCo.DATA_FILE_NAME_DTF);
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach(this::validateAndTransform);
    }

    private void transform(String nseFileName, String praFileName) {
        String source = Source_Data_Dir + File.separator + nseFileName;
        String target = Target_Data_Dir + File.separator + praFileName;
        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("DM | already transformed - {}", target);
        } else if (nseFileUtils.isFilePresent(source)) {
            try {
                int outputRowsCount = transformToDmCsv(source, target);
                LOGGER.info("DM | source transformed - {}, output rows {}", target, outputRowsCount);
            } catch (Exception e) {
                LOGGER.warn("DM | Error while transforming file: {} {}", source, e);
            }
        } else {
            LOGGER.info("DM | source not found - {}", source);
        }
    }
    private void validateAndTransform(String sourceFileName, String targetFileName) {
        String source = Source_Data_Dir + File.separator + sourceFileName;
        String target = Target_Data_Dir + File.separator + targetFileName;

        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("DM | already transformed - {}", target);
            return;
        }

        if (nseFileUtils.isFileAbsent(source)) {
            LOGGER.info("MAT | source not found - {}", source);
            return;
        }

        long bytes = 0;
        try {
            bytes = Files.size(Paths.get(source));
        } catch (IOException e) {
            LOGGER.error("MAT | error reading file - {}", source);
        }

        if (bytes == 0) {
            LOGGER.warn("MAT | file size is ZERO (may be holiday file) - {}", source);
            return;
        }

        try {
            int outputRowsCount = transformToDmCsv(source, target);
            LOGGER.info("DM | transformed - {}, output rows {}", target, outputRowsCount);
        } catch (Exception e) {
            LOGGER.warn("DM | Error while transforming file: {} {}", source, e);
        }

    }
    private int transformToDmCsv(String source, String target) {
        int firstIndex = source.lastIndexOf("_");
        String tradeDate = DateUtils.transformDate(source.substring(firstIndex+1, firstIndex+9));
        String csvFileName = targetFilePrefix + tradeDate + targetFileExtension;

        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicInteger outGoingRows = new AtomicInteger();

        File csvOutputFile = new File(target);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            try (Stream<String> stream = Files.lines(Paths.get(source))) {
                stream.filter(line-> atomicInteger.incrementAndGet() > 3)
                        .map(row -> {
                            if(atomicInteger.get() == 4) {
                                outGoingRows.incrementAndGet();
                                return "RecType,SrNo,Symbol,SecurityType,TradedQty,DeliverableQty,DeliveryToTradeRatio,TradeDate";
                            } else {
                                outGoingRows.incrementAndGet();
                                return row + "," + tradeDate;
                            }
                        }).forEach(pw::println);
            } catch (IOException e) {
                LOGGER.warn("Error in MAT entry:", e);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Error:", e);
        }
        return  outGoingRows.intValue();
    }

}
