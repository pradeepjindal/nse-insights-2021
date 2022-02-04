package org.pra.nse.csv.transformation;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.PraCons;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.FileUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class FoTransformer extends BaseTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoTransformer.class);

    private final String sourceDirName = NseCons.FO_DIR_NAME;
    private final String sourceFilePrefix = NseCons.NSE_FO_FILE_PREFIX;
    private final String sourceFileExtension = ApCo.ZIP_FILE_EXT;

    private final String targetDirName = PraCons.FO_DIR_NAME;
    private final String targetFilePrefix = PraCons.PRA_FO_FILE_PREFIX;
    private final String targetFileExtension = ApCo.CSV_FILE_EXT;

    private final LocalDate defaultDate = ApCo.TRANSFORM_NSE_FROM_DATE;

    private final String Source_Data_Dir = ApCo.ROOT_DIR + File.separator + sourceDirName;
    private final String Target_Data_Dir = ApCo.ROOT_DIR + File.separator + targetDirName;

    public FoTransformer(TransformationHelper transformationHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(transformationHelper, nseFileUtils, praFileUtils);
    }


    public void transformAll() {
        transformFromDate(ApCo.NSE_FO_FILE_AVAILABLE_FROM_DATE);
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
                NseCons.NSE_FO_FILE_NAME_DATE_FORMAT,
                sourceFilePrefix,
                sourceFileExtension);
        //filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(dataDir, null, null));
        //
        Map<String, String> filePairMap;
        filePairMap = TransformationHelper.prepareFileNames(sourceFileNames,
                NseCons.NSE_FO_FILE_NAME_DATE_REGEX,
                NseCons.NSE_FO_FILE_NAME_DATE_FORMAT,
                targetFilePrefix,
                targetFileExtension,
                ApCo.DATA_FILE_NAME_DTF);
        return filePairMap;
    }

    private void looper(Map<String, String> filePairMap) {
        filePairMap.forEach(this::validateAndTransform);
    }

    private void transformForDateWIP(LocalDate forDate) {
        // special condition: nse file is corrupt for 23-Sep-2021 hence uploading the 22-Sep-2021
        if(forDate.toString().equals("2021-11-23")) {
            LOGGER.warn("FO | transform - special condition: nse file is corrupt for 23-Sep-2021 hence skipping");
        }
        String sourceFileName = FileUtils.constructFileName(forDate, NseCons.NSE_FO_FILE_NAME_DATE_FORMAT,
                sourceFilePrefix, "", sourceFileExtension);
        String targetFileName = FileUtils.constructFileName(forDate, PraCons.PRA_FILE_NAME_DATE_FORMAT,
                targetFilePrefix, "", targetFileExtension);

        String fileNameToBeExtractedFromZip = sourceFileName.split("\\.")[0] + ".csv";
        String source = Source_Data_Dir + File.separator + sourceFileName;
        String target = Target_Data_Dir+"-csv" + File.separator + targetFileName;

    }
    private void validateAndTransform(String sourceFileName,
                                      String targetFileName) {
        String fileNameToBeExtractedFromZip = sourceFileName.split("\\.")[0] + ".csv";
        String source = Source_Data_Dir + File.separator + sourceFileName;
        String target = Target_Data_Dir+"-csv" + File.separator + targetFileName;

        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("FO | already copied - {}", target);
            return;
        }

        if (nseFileUtils.isFileAbsent(source)) {
            LOGGER.info("FO | source zip not found - {}", source);
            return;
        }

        long bytes = 0;
        try {
            bytes = Files.size(Paths.get(source));
        } catch (IOException e) {
            LOGGER.error("FO | error reading file - {}", source);
        }

        if (bytes == 0) {
            LOGGER.warn("FO | file size is ZERO (may be holiday file) - {}", source);
            return;
        }

        try {
            extractCsvFromZip(Source_Data_Dir, sourceFileName, fileNameToBeExtractedFromZip, Source_Data_Dir+"-csv", targetFileName);
            transformToFoCsv(Source_Data_Dir+"-csv", targetFileName, Target_Data_Dir, targetFileName);
            LOGGER.info("FO | transformed");
        } catch (Exception e) {
            LOGGER.warn("FO | Error while transforming file: {} {}", source, e);
        }
    }

    private void extractCsvFromZip(String sourceDataDir,
                                  String sourceFileName,
                                  String fileNameToBeExtractedFromZip,
                                  String targetDataDir,
                                  String targetFileName) {
        String source = sourceDataDir + File.separator + sourceFileName;
        String target = targetDataDir + File.separator + targetFileName;
        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("FO | extracted already - {}", target);
        } else if (nseFileUtils.isFilePresent(source)) {
            try {
                //TODO pass on the target file name
                nseFileUtils.extractFileFromZipUsingNewIo(
                        sourceDataDir,
                        sourceFileName,
                        fileNameToBeExtractedFromZip,
                        targetDataDir,
                        targetFileName);
                LOGGER.info("FO | extracted - {}", target);
            } catch (FileNotFoundException fnfe) {
                LOGGER.info("FO | file not found - {}", source);
            } catch (IOException e) {
                LOGGER.info("FO | failed to process - {}", source);
                LOGGER.warn("FO | error while unzipping file:", e);
            }
        } else {
            LOGGER.error("FO | source not found ({})", source);
        }

    }


    public void extractFileNewIo(Path zipFile, String fileName, Path outputFile) {
        Path inFile = Paths.get("D:\\nseEnv-2021\\nse-data\\nse-fo\\fo01012015.zip");
        Path outFile = Paths.get("D:\\nseEnv-2021\\nse-data\\nse-fo\\fo01012015.csv");
        try (FileSystem fileSystem = FileSystems.newFileSystem(inFile)) {
            Path fileToExtract = fileSystem.getPath("fo01012015.csv");
            Files.copy(fileToExtract, outFile);
        } catch (IOException ioe) {
            LOGGER.error("", ioe);
        }
    }

    public void extractFileOldIo() {
        try {
            String fileToBeExtracted="fo01012015.csv";
            String zipPackage="D:\\nseEnv-2021\\nse-data\\nse-fo\\fo01012015.zip";
            OutputStream out = new FileOutputStream("D:\\nseEnv-2021\\nse-data\\nse-fo\\fo01012015.csv");
            FileInputStream fileInputStream = new FileInputStream(zipPackage);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream );
            ZipInputStream zin = new ZipInputStream(bufferedInputStream);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals(fileToBeExtracted)) {
                    byte[] buffer = new byte[9000];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    break;
                }
            }
            zin.close();
        } catch(IOException ioe) {
            LOGGER.error("", ioe);
        }
    }

    private void transformToFoCsv(String sourceDataDir,
                                  String sourceFileName,
                                  String targetDataDir,
                                  String targetFileName) {
        List<String> csvLines = readCsvAsRawLines(sourceDataDir, sourceFileName);
        writeFoCsv(csvLines, targetDataDir, targetFileName);
    }

    private List<String> readCsvAsRawLines(String sourceDataDir, String sourceFileName) {
        String source = sourceDataDir + File.separator + sourceFileName;

        if (nseFileUtils.isFileAbsent(source)) {
            LOGGER.info("FO | source csv not found - {}", source);
            return Collections.emptyList();
        }

        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        records.remove(0);
        records.remove(records.size()-1);

        String instrument;
        String symbol;
        String expiry;
        String value;
        String quantity;
        String contracts;
        //
        String instrumentString;
        String symbolString;
        LocalDate expiryLocalDate;
        BigDecimal valueBigDecimal;
        int quantityInt;
        int contractsInt;
        int lotSize;

        List<String> csvOutputLines = new ArrayList<>();
        csvOutputLines.add("Symbol,ExpiryDate,Instrument,Turnover,Quantity,Contracts,LotSize");

        String csvLine;
        Set<LocalDate> dates = new HashSet<>();
        for(List<String> raw: records) {
            instrument = raw.get(0).trim();
            symbol = raw.get(1).trim();
            expiry = raw.get(2).trim();
            value = raw.get(8).trim();
            quantity = raw.get(9).trim();
            contracts = raw.get(10).trim();
            //
            instrumentString = instrument;
            symbolString = symbol;
            String[] expiryArray = expiry.split("/");
            expiryLocalDate = LocalDate.of(
                    Integer.valueOf(expiryArray[2]),
                    Integer.valueOf(expiryArray[1]),
                    Integer.valueOf(expiryArray[0])
            );
            dates.add(expiryLocalDate);
            //
            valueBigDecimal = new BigDecimal(value);
            quantityInt = Integer.valueOf(quantity);
            contractsInt = Integer.valueOf(contracts);
            lotSize = quantityInt / contractsInt;
            //
            csvLine = symbolString + "," + expiryLocalDate + "," + instrumentString + "," + valueBigDecimal + "," + quantityInt + "," + contractsInt + "," + lotSize;
            csvOutputLines.add(csvLine);
//            LOGGER.info("{}, {}, {}", symbolString, lotSize, expiryLocalDate);
        }
//        LOGGER.info("{}", dates);
        return csvOutputLines;
    }

    private void writeFoCsv(List<String> csvOutputLines, String targetDataDir, String targetFileName) {
        String target = targetDataDir + File.separator + targetFileName;

        if(nseFileUtils.isFilePresent(target)) {
            LOGGER.info("FO | transformed already - {}", target);
            return;
        }

        File csvOutputFile = new File(target);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for (String csvLine: csvOutputLines) {
                pw.write(csvLine + "\n");
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Error:", e);
        }
    }

}
