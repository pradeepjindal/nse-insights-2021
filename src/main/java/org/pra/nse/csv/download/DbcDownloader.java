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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DbcDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbcDownloader.class);

    private final String Base_Url = NseCons.DBC_BASE_URL;
    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + NseCons.DBC_DIR_NAME;
    private final String File_Prefix = NseCons.NSE_DBC_FILE_PREFIX;
    private final String File_Suffix = NseCons.NSE_DBC_FILE_SUFFIX;
    private final String File_Ext = NseCons.NSE_DBC_FILE_EXT;
    private final String File_Date_Regex = NseCons.NSE_DBC_FILE_NAME_DATE_REGEX;
    private final String File_Date_Format = NseCons.NSE_DBC_FILE_NAME_DATE_FORMAT;
    private final DateTimeFormatter File_Date_Dtf = NseCons.DBC_FILE_NAME_DTF;
    private final String Data_Date_Regex = null;
    private final String Data_Date_Format = null;
    private final DateTimeFormatter Data_Date_Dtf = null;

    private final DownloadHelper downloadHelper;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;


    public DbcDownloader(NseFileUtils nseFileUtils, PraFileUtils praFileUtils, DownloadHelper downloadHelper) {
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
        //String str = praFileUtils.getLatestFileNameFor(Data_Dir, File_Prefix, File_Ext, 1);
        String str = praFileUtils.getLatestFileNameFor(Data_Dir, File_Prefix, File_Ext,  1, LocalDate.now(), ApCo.DOWNLOAD_NSE_FROM_DATE, File_Date_Dtf);

        //LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str);
        LocalDate dateOfLatestFile = DateUtils.getLocalDateFromPath(str, File_Date_Regex, File_Date_Format);

        List<String> filesDownloadUrls = prepareFileUrls(dateOfLatestFile.plusDays(1));
        if(filesDownloadUrls == null || filesDownloadUrls.size() == 0) return;

        LocalDate dateOfNextFile = DateUtils.getLocalDateFromPath(filesDownloadUrls.get(0), File_Date_Regex, File_Date_Format);
        if(filesDownloadUrls.size() == 1 && dateOfNextFile.isBefore(LocalDate.now())) {
            downloadFromUrl(filesDownloadUrls.get(0));
        } else {
            looper(filesDownloadUrls);
        }
    }


    private List<String> prepareFileUrls(LocalDate downloadFromDate) {
        List<String> filesToBeDownloaded = nseFileUtils.constructFileNames(downloadFromDate, File_Date_Format, File_Prefix, File_Suffix + File_Ext);
        filesToBeDownloaded.removeAll(nseFileUtils.fetchFileNames(Data_Dir, null, null));
        return nseFileUtils.constructFileDownloadUrl(Base_Url, filesToBeDownloaded);
    }

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
                //() -> (Data_Dir + File.separator + fileUrl.substring(65)),
                () -> (Data_Dir + File.separator + fileUrl.substring(Base_Url.length())),
                incomingFilePathAndName -> {
                    LOGGER.info("PASSING: no processing required: {}", incomingFilePathAndName);
                });
    }

}
