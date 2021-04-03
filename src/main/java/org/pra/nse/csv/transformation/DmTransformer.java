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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class DmTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmTransformer.class);

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + NseCons.DM_DIR_NAME;
    private final String Target_Data_Dir = ApCo.ROOT_DIR + File.separator + "pra-dm";


    public DmTransformer(TransformationHelper transformationHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(transformationHelper, nseFileUtils, praFileUtils);
    }


    public void transformFromDefaultDate() {
        transformFromDate(ApCo.TRANSFORM_NSE_FROM_DATE);
    }
    public void transformFromDate(LocalDate fromDate) {
        Map<String, String> filePairMap = prepare(fromDate);
        looper(filePairMap);
    }

    public void transformFromLastDate() {
        String str = praFileUtils.getLatestFileNameFor(Target_Data_Dir, ApCo.PRA_DM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str);
        Map<String, String> filePairMap = prepare(dateOfLatestFile);
        looper(filePairMap);
    }


    private Map<String, String> prepare(LocalDate fromDate) {
        List<String> sourceFileNames = nseFileUtils.constructFileNames(
                fromDate,
                NseCons.NSE_DM_FILE_NAME_DATE_FORMAT,
                NseCons.NSE_DM_FILE_PREFIX,
                NseCons.NSE_DM_FILE_EXT);
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
                ApCo.PRA_DM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, ApCo.DATA_FILE_NAME_DTF);
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach( (nseFileName, praFileName) -> {
            //transform(nseFileName, praFileName);
            transformNew(nseFileName, praFileName);
        });
    }

    private void transform(String nseFileName, String praFileName) {
        String source = Data_Dir + File.separator + nseFileName;
        String target = Data_Dir + File.separator + praFileName;
        if(nseFileUtils.isFileExist(target)) {
            LOGGER.info("DM | already transformed - {}", target);
        } else if (nseFileUtils.isFileExist(source)) {
            try {
                int outputRowsCount = transformToDmCsv(source, Data_Dir);
                LOGGER.info("DM | source transformed - {}, output rows {}", target, outputRowsCount);
            } catch (Exception e) {
                LOGGER.warn("DM | Error while transforming file: {} {}", source, e);
            }
        } else {
            LOGGER.info("DM | source not found - {}", source);
        }
    }
    private void transformNew(String nseFileName, String praFileName) {
        String source = Data_Dir + File.separator + nseFileName;
        String target = Target_Data_Dir + File.separator + praFileName;
        if(nseFileUtils.isFileExist(target)) {
            LOGGER.info("DM | already transformed - {}", target);
        } else if (nseFileUtils.isFileExist(source)) {
            try {
                int outputRowsCount = transformToDmCsv(source, Target_Data_Dir);
                LOGGER.info("DM | transformed - {}, output rows {}", target, outputRowsCount);
            } catch (Exception e) {
                LOGGER.warn("DM | Error while transforming file: {} {}", source, e);
            }
        } else {
            LOGGER.info("DM | source not found - {}", source);
        }
    }
    private int transformToDmCsv(String downloadedDirAndFileName, String tgtDataDir) {
        int firstIndex = downloadedDirAndFileName.lastIndexOf("_");
        String tradeDate = DateUtils.transformDate(downloadedDirAndFileName.substring(firstIndex+1, firstIndex+9));
        String csvFileName =
                ApCo.PRA_DM_FILE_PREFIX
                + tradeDate
                + ApCo.REPORTS_FILE_EXT;
        //String toFile = ApCo.ROOT_DIR + File.separator + NseCons.DM_DIR_NAME + File.separator + csvFileName;
        String toFile = tgtDataDir + File.separator + csvFileName;
        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicInteger outGoingRows = new AtomicInteger();
        File csvOutputFile = new File(toFile);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            try (Stream<String> stream = Files.lines(Paths.get(downloadedDirAndFileName))) {
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
                LOGGER.warn("Error in MAT entry: {}", e);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Error: {}", e);
        }
        return  outGoingRows.intValue();
    }

}
