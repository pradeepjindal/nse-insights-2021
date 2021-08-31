package org.pra.nse.csv.transformation;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class IdxTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdxTransformer.class);

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + NseCons.IDX_DIR_NAME;
    private final String Target_Data_Dir = ApCo.ROOT_DIR + File.separator + "pra-nx";


    public IdxTransformer(TransformationHelper transformationHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(transformationHelper, nseFileUtils, praFileUtils);
        //DirUtils.ensureFolder("pra-nx");
    }


    public void transformAll() {
        transformFromDate(ApCo.NSE_IDX_FILE_AVAILABLE_FROM_DATE);
    }
    public void transformFromDefaultDate() {
        transformFromDate(ApCo.TRANSFORM_NSE_FROM_DATE);
    }
    public void transformFromDate(LocalDate fromDate) {
        Map<String, String> filePairMap = prepare(fromDate);
        looper(filePairMap);
    }
    public void transformFromLatestDate() {
        String str = praFileUtils.getLatestFileNameFor(Target_Data_Dir, ApCo.PRA_IDX_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str);
        Map<String, String> filePairMap = prepare(dateOfLatestFile);
        looper(filePairMap);
    }


    private Map<String, String> prepare(LocalDate fromDate) {
        List<String> sourceFileNames = nseFileUtils.constructFileNames(
                fromDate,
                NseCons.NSE_IDX_FILE_NAME_DATE_FORMAT,
                NseCons.NSE_IDX_FILE_PREFIX,
                NseCons.NSE_IDX_FILE_EXT);
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
                NseCons.NSE_IDX_FILE_NAME_DATE_REGEX, NseCons.NSE_IDX_FILE_NAME_DATE_FORMAT,
                ApCo.PRA_IDX_FILE_PREFIX, ApCo.DEFAULT_FILE_EXT, ApCo.DATA_FILE_NAME_DTF);
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach(this::validateAndTransform);
    }

    private void transform(String nseFileName, String praFileName) {
        String source = Data_Dir + File.separator + nseFileName;
        String target = Target_Data_Dir + File.separator + praFileName;
        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("IDX | already transformed - {}", target);
        } else if (nseFileUtils.isFilePresent(source)) {
            try {
                Map.Entry<Integer, Integer> incoming_And_Outgoing_Rows = transformToIdxCsv(source, Target_Data_Dir);
                if(incoming_And_Outgoing_Rows.getKey() == incoming_And_Outgoing_Rows.getValue())
                    LOGGER.info("IDX | transformed - {}, input rows: {}, output rows {}",
                            target, incoming_And_Outgoing_Rows.getKey(), incoming_And_Outgoing_Rows.getValue());
                else
                    LOGGER.error("IDX | transformed - {}, input rows: {}, output rows {}",
                            target, incoming_And_Outgoing_Rows.getKey(), incoming_And_Outgoing_Rows.getValue());
            } catch (Exception e) {
                LOGGER.warn("IDX | Error while transforming file: {} {}", source, e);
            }
        } else {
            LOGGER.info("IDX | source not found - {}", source);
        }
    }

    private void validateAndTransform(String nseFileName, String praFileName) {
        String source = Data_Dir + File.separator + nseFileName;
        String target = Target_Data_Dir + File.separator + praFileName;

        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("IDX | already transformed - {}", target);
            return;
        }

        if (nseFileUtils.isFileAbsent(source)) {
            LOGGER.info("IDX | source not found - {}", source);
            return;
        }

        long bytes = 0;
        try {
            bytes = Files.size(Paths.get(source));
        } catch (IOException e) {
            LOGGER.error("IDX - error reading file - {}", source);
        }

        if (bytes == 0) {
            LOGGER.warn("IDX file size is ZERO (may be holiday file) - {}", source);
            return;
        }

        try {
            Map.Entry<Integer, Integer> incoming_And_Outgoing_Rows = transformToIdxCsv(source, Target_Data_Dir);
            if(incoming_And_Outgoing_Rows.getKey() == incoming_And_Outgoing_Rows.getValue())
                LOGGER.info("IDX | transformed - {}, input rows: {}, output rows {}",
                        target, incoming_And_Outgoing_Rows.getKey(), incoming_And_Outgoing_Rows.getValue());
            else
                LOGGER.error("IDX | transformed - {}, input rows: {}, output rows {}",
                        target, incoming_And_Outgoing_Rows.getKey(), incoming_And_Outgoing_Rows.getValue());

        } catch (Exception e) {
            LOGGER.warn("IDX | Error while transforming file: {} {}", source, e);
        }

    }

    private Map.Entry<Integer, Integer> transformToIdxCsv(String downloadedDirAndFileName, String tgtDataDir) {
        int firstIndex = downloadedDirAndFileName.lastIndexOf("_");
        String tradeDate = DateUtils.transformDate(downloadedDirAndFileName.substring(firstIndex+1, firstIndex+9));
        String csvFileName =
                ApCo.PRA_IDX_FILE_PREFIX
                        + tradeDate
                        + ApCo.DEFAULT_FILE_EXT;
        //String toFile = ApCo.ROOT_DIR + File.separator + NseCons.IDX_DIR_NAME + File.separator + csvFileName;
        String toFile = tgtDataDir + File.separator + csvFileName;
        AtomicInteger inComingRows = new AtomicInteger();
        AtomicInteger outGoingRows = new AtomicInteger();
        Map.Entry<Integer, Integer> incomingAndOutgoingRows = new AbstractMap.SimpleEntry<>(inComingRows.get(), outGoingRows.get());

        File csvOutputFile = new File(toFile);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            try (Stream<String> stream = Files.lines(Paths.get(downloadedDirAndFileName))) {
                //stream.filter(line-> atomicInteger.incrementAndGet() > 0 && line.indexOf(",-,") == -1)
                stream.filter(line-> inComingRows.incrementAndGet() > 0)
                        .map(row -> {
                            if(inComingRows.get() == 1) {
                                outGoingRows.incrementAndGet();
                                return "IdxName,TradeDate,open,high,low,close,PointsChgAbs,PointsChgPct,volume,turnOverInCrore,pe,pb,divYield";
                            } else {
                                outGoingRows.incrementAndGet();
                                //LOGGER.info("{}", row);
                                String newRow = row.replaceAll(",-", ",0");
                                //LOGGER.info("{}", newRow);
                                return newRow;
                            }
                        }).forEach(pw::println);
            } catch (IOException e) {
                LOGGER.warn("Error in IDX entry:", e);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Error:", e);
        }
        incomingAndOutgoingRows = new AbstractMap.SimpleEntry<>(inComingRows.get(), outGoingRows.get());
        return incomingAndOutgoingRows;
    }

    private boolean qualified(String csvLine) {
        if(csvLine.contains("Nifty50 Dividend Points")) return false;
        if(csvLine.contains("Shariah")) return false;
        if(csvLine.contains("Nifty Low Volatility 50")) return false;
        if(csvLine.contains("Nifty High Beta 50")) return false;
        if(csvLine.contains("NIFTY Quality Low-Volatility 30")) return false;
        if(csvLine.contains("NIFTY Alpha Quality Low-Volatility 30")) return false;
        if(csvLine.contains("NIFTY Alpha Quality Value Low-Volatility 30")) return false;


        if(csvLine.contains("NIFTY Midcap150 Quality 50")) return false;
        if(csvLine.contains("NIFTY LargeMidcap 250")) return false;
        if(csvLine.contains("NIFTY SME EMERGE")) return false;
        if(csvLine.contains("Nifty Oil & Gas")) return false;
        if(csvLine.contains("Nifty Healthcare Index")) return false;
        if(csvLine.contains("Nifty500 Multicap 50:25:25")) return false;
        if(csvLine.contains("NIFTY100 ESG")) return false;
        if(csvLine.contains("NIFTY100 Enhanced ESG")) return false;
        if(csvLine.contains("NIFTY500 Value 50")) return false;
        if(csvLine.contains("Tata")) return false;
        if(csvLine.contains("Tata")) return false;
        if(csvLine.contains("Tata")) return false;



        if(csvLine.contains("Mahindra")) return false;

        if(csvLine.contains("Tata")) return false;
        if(csvLine.contains("Nifty Consumer Durables")) return false;
        if(csvLine.contains("Aditya Birla")) return false;
        if(csvLine.contains("Rate Index")) return false;

        return true;
    }
}
