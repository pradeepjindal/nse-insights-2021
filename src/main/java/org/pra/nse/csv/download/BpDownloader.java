package org.pra.nse.csv.download;

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class BpDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BpDownloader.class);

    private final String Base_Url = NseCons.BP_BASE_URL;
    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + NseCons.BP_DIR_NAME;
    private final String File_Prefix = NseCons.NSE_BP_FILE_PREFIX;
    private final String File_Suffix = NseCons.NSE_BP_FILE_SUFFIX;
    private final String File_Ext = NseCons.NSE_BP_FILE_EXT;
    private final String File_Date_Regex = NseCons.NSE_BP_FILE_NAME_DATE_REGEX;
    private final String File_Date_Format = NseCons.NSE_BP_FILE_NAME_DATE_FORMAT;
    //private final DateTimeFormatter File_Date_Dtf = ApCo.BP_FILE_NAME_DTF;
    private final String Data_Date_Regex = null;
    private final String Data_Date_Format = null;
    private final DateTimeFormatter Data_Date_Dtf = null;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;

    private final DownloadHelper downloadHelper;

    public BpDownloader(NseFileUtils nseFileUtils, PraFileUtils praFileUtils, DownloadHelper downloadHelper) {
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.downloadHelper = downloadHelper;
    }

    public void downloadFromDefaultDate() {
        downloadFromDate(ApCo.DOWNLOAD_NSE_FROM_DATE);
    }
    public void downloadFromDate(LocalDate fromDate) {
        List<String> filesDownloadUrl = prepareFileUrls(fromDate);
        looper(filesDownloadUrl);
    }

    public void downloadFromLastDate() {
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, ApCo.PRA_BP_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str);
        List<String> filesDownloadUrl = prepareFileUrls(dateOfLatestFile.plusDays(1));
        looper(filesDownloadUrl);
    }

    private List<String> prepareFileUrls(LocalDate fromDate) {
        List<String> filesToBeDownloaded = nseFileUtils.constructFileNames(fromDate, File_Date_Format, File_Prefix, File_Suffix + File_Ext);
        filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(Data_Dir, null, null));
        return nseFileUtils.constructFileDownloadUrl(Base_Url, filesToBeDownloaded);
    }

//    private void looper(List<String> urlListToBeDownloaded) {
//        if(downloadHelper.shouldDownload(urlListToBeDownloaded)) {
//            urlListToBeDownloaded.stream().forEach( fileUrl -> {
//                downloadHelper.downloadFile(fileUrl, Data_Dir,
//                        () -> (Data_Dir + File.separator + fileUrl.substring(ApCo.BP_BASE_URL.length()+1)),
//                        filePathAndName -> {
//                            transformToCsv(filePathAndName);
//                        });
//            });
//        }
//    }

//    private void looper(List<String> urlListToBeDownloaded) {
//        if(downloadHelper.shouldDownload(urlListToBeDownloaded)) {
//            urlListToBeDownloaded.stream().forEach( fileUrl -> {
//                download(fileUrl);
//            });
//        }
//    }
    private void looper(List<String> urlListToBeDownloaded) {
        urlListToBeDownloaded.stream().filter( fileUrl -> {
            LocalDate forDate = DateUtils.getLocalDateFromPath(fileUrl, File_Date_Regex, File_Date_Format);
            LOGGER.info("filter - forDate: {} ({})", forDate, fileUrl);
            return downloadHelper.timeFilter(forDate);
        }).forEach( filteredFileUrl -> {
            LOGGER.info("download - forDate: {}", filteredFileUrl);
            downloadFromUrl(filteredFileUrl);
        });
    }

    private void downloadFromUrl(String fileUrl) {
        downloadHelper.downloadFile(fileUrl, Data_Dir,
                () -> (Data_Dir + File.separator + fileUrl.substring(Base_Url.length()+1)),
                filePathAndName -> {
                    transformToCsv(filePathAndName);
                });
    }

    private void transformToCsv(String downloadedDirAndFileName) {
        int firstIndex = downloadedDirAndFileName.lastIndexOf("_");
        String csvFileName = ApCo.PRA_BP_FILE_PREFIX
                + DateUtils.transformDate(downloadedDirAndFileName.substring(firstIndex+1, firstIndex+9))
                + ApCo.REPORTS_FILE_EXT;
        String toFile = ApCo.ROOT_DIR + File.separator + NseCons.BP_DIR_NAME + File.separator + csvFileName;
        AtomicInteger atomicInteger = new AtomicInteger();
        File csvOutputFile = new File(toFile);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            try (Stream<String> stream = Files.lines(Paths.get(downloadedDirAndFileName))) {
                stream.filter(line->atomicInteger.incrementAndGet() > 1)
                        .map(row -> {
                    if(atomicInteger.get() == 2) {
                        return "ClientType,FutIdxLong,FutIdxShort,FutStkLong,FutStkShort,OptIdxCallLong,OptIdxPutLong,OptIdxCallShort,OptIdxPutShort,OptStkCallLong,OptStkPutLong,OptStkCallShort,OptStkPutShort,TotalLongContracts,TotalShortContracts";
                    } else {
                        return row;
                    }
                }).forEach(pw::println);
            } catch (IOException e) {
                LOGGER.warn("some error in fao entry: {}", e);
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("some error: {}", e);
        }
    }

}
