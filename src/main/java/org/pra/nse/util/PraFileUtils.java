package org.pra.nse.util;


import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.ProCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class PraFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PraFileUtils.class);


    public String getLatestFileNameFor(String fileDir, String filePrefix, String fileExt, int occurrence) {
        return getLatestFileNameFor(fileDir, filePrefix, fileExt, occurrence, LocalDate.now());
    }
    public String getLatestFileNameFor(String fileDir, String filePrefix, String fileExt, int occurrence, LocalDate searchBackFromThisDate) {
        return getLatestFileNameFor(fileDir, filePrefix, fileExt, occurrence, searchBackFromThisDate, ApCo.NSE_CM_FILE_AVAILABLE_FROM_DATE, ApCo.PRA_DTF);
    }
    public String getLatestFileNameFor(String fileDir, String filePrefix, String fileExt, int fileOccurrence,
                                       LocalDate searchBackFromThisDate, LocalDate toThisDate, DateTimeFormatter fileNameDtf) {
        LocalDate rollingDate = searchBackFromThisDate;
        File file;
        String fileName;
        String filePathWithFileName = null;
        for(int i=0; i<fileOccurrence; i++) {
            do {
                fileName = filePrefix + fileNameDtf.format(rollingDate) + fileExt;
                //LOGGER.info("getLatestFileName | fileName: {}", fileName);
                filePathWithFileName = fileDir + File.separator + fileName;
                //LOGGER.info("getLatestFileName | filePathWithFileName: {}", filePathWithFileName);
                rollingDate = rollingDate.minusDays(1);
                file = new File(filePathWithFileName);
                if(file.exists()) break;
                filePathWithFileName = null;
                if(rollingDate.compareTo(toThisDate.minusDays(5)) < 0) break;
            } while(true);
        }
        return filePathWithFileName;
    }

//    public String getLatestFileNameFor(String fileDir, String filePrefix, String fileExt, int occurrence,
//                                       LocalDate searchBackFromThisDate, LocalDate toThisDate,
//                                       DateTimeFormatter fileNameDtf) {
//        LocalDate rollingDate = searchBackFromThisDate;
//        File file;
//        String fileName = null;
//        String filePathWithFileName = null;
//        for(int i=0; i<occurrence; i++) {
//            do {
//                fileName = filePrefix + fileNameDtf.format(rollingDate) + fileExt;
//                //LOGGER.info("getLatestFileName | fileName: {}", fileName);
//                filePathWithFileName = fileDir + File.separator + fileName;
//                //LOGGER.info("getLatestFileName | filePathWithFileName: {}", filePathWithFileName);
//                rollingDate = rollingDate.minusDays(1);
//                file = new File(filePathWithFileName);
//                if(file.exists()) break;
//                filePathWithFileName = null;
//                //if(rollingDate.compareTo(ApCo.DOWNLOAD_FROM_DATE.minusDays(5)) < 0) break;
//                if(rollingDate.compareTo(toThisDate.minusDays(5)) < 0) break;
//            } while(true);
//        }
//        return filePathWithFileName;
//    }

    private String dateToString(LocalDate localDate) {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                // case insensitive to parse JAN and FEB
                //.parseCaseInsensitive()
                // add pattern
                .appendPattern("yyyy-MM-dd")
                // create formatter (use English Locale to parse month names)
                .toFormatter(Locale.ENGLISH);
        return LocalDate.parse(localDate.toString(), dtf).toString();
    }

    public LocalDate getLatestNseDateCDF() {
        LocalDate cmDate = getLatestCmLocalDate();
        LocalDate dmDate = getLatestDmLocalDate();
        LocalDate fmDate = getLatestFmLocalDate();

        List<LocalDate> dates = new ArrayList<>();
        dates.add(cmDate);
        dates.add(dmDate);
        dates.add(fmDate);
        Collections.sort(dates);
        return dates.get(0);
    }

    public LocalDate getLatestNseDateCD() {
        LocalDate cmDate = getLatestCmLocalDate();
        LocalDate dmDate = getLatestDmLocalDate();

        List<LocalDate> dates = new ArrayList<>();
        dates.add(cmDate);
        dates.add(dmDate);
        Collections.sort(dates);
        return dates.get(0);
    }

    public String validateDownloadCDF() {
        String cmDate = getLatestCmStringDate();
        String mtDate = getLatestDmStringDate();
        String foDate = getLatestFmStringDate();

        if (cmDate == null || mtDate == null || foDate == null) {
            return null;
        } else if (cmDate.equals(mtDate) && mtDate.equals(foDate)) {
            return cmDate;
        } else {
            LOGGER.warn("Not All files are available: cm=[cm-{}], dm=[dm-{}], fm=[fm-{}]", cmDate, mtDate, foDate);
            LOGGER.info("fm=[fm-{}]", foDate);
            LOGGER.info("dm=[dm-{}]", mtDate);
            LOGGER.info("cm=[cm-{}]", cmDate);
            //throw new RuntimeException("All Files Does Not Exist: ABORTING");
            return null;
        }
    }

    public String validateDownloadCD() {
        String cmDate = getLatestCmStringDate();
        String dmDate = getLatestDmStringDate();

        if (cmDate == null || dmDate == null) {
            return null;
        } else if (cmDate.equals(dmDate)) {
            return cmDate;
        } else {
            LOGGER.warn("Not All files are available: cm=[cm-{}], dm=[dm-{}]", cmDate, dmDate);
            LOGGER.info("cm=[cm-{}]", cmDate);
            LOGGER.info("dm=[dm-{}]", dmDate);
            //throw new RuntimeException("All Files Does Not Exist: ABORTING");
            return null;
        }
    }

    private LocalDate getLatestCmLocalDate() {
        return DateUtils.toLocalDate(getLatestCmStringDate());
    }
    private String getLatestCmStringDate() {
        String cmDate = getLatestFileNameFor(NseCons.CM_FILES_PATH, ApCo.PRA_CM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        cmDate = ProCo.extractDate(cmDate);
        return cmDate;
    }

    private LocalDate getLatestDmLocalDate() {
        return DateUtils.toLocalDate(getLatestDmStringDate());
    }
    private String getLatestDmStringDate() {
        String mtDate = getLatestFileNameFor(NseCons.DM_FILES_PATH, ApCo.PRA_DM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        mtDate = ProCo.extractDate(mtDate);
        return mtDate;
    }

    private LocalDate getLatestFmLocalDate() {
        return DateUtils.toLocalDate(getLatestFmStringDate());
    }
    private String getLatestFmStringDate() {
        String foDate = getLatestFileNameFor(NseCons.FM_FILES_PATH, ApCo.PRA_FM_FILE_PREFIX, ApCo.REPORTS_FILE_EXT, 1);
        foDate = ProCo.extractDate(foDate);
        return foDate;
    }

}
