package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;

public abstract class BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUploader.class);

    private final PraFileUtils praFileUtils;

    private final String fileDirName;
    private final String filePrefix;

    protected BaseUploader(PraFileUtils praFileUtils, String fileDirName, String filePrefix) {
        this.praFileUtils = praFileUtils;
        this.fileDirName = fileDirName;
        this.filePrefix = filePrefix;
    }


    public void uploadFromDefaultDate() {
        uploadFromDate(ApCo.UPLOAD_NSE_FROM_DATE);
    }
    public void uploadFromDate(LocalDate fromDate) {
        looper(fromDate);
    }

    public void uploadFromLastDate() {
        String dataDir = ApCo.ROOT_DIR + File.separator + fileDirName;
        String str = praFileUtils.getLatestFileNameFor(dataDir, filePrefix, ApCo.DATA_FILE_EXT, 1);
        LocalDate dt = str == null ? LocalDate.now() : DateUtils.getLocalDateFromPath(str);
        looper(dt);
    }


    protected abstract void uploadForDate(LocalDate forDate);


    private void looper(LocalDate fromDate) {
        LocalDate today = LocalDate.now();
        LocalDate processingDate = fromDate.minusDays(1);
        do {
            processingDate = processingDate.plusDays(1);
            LOGGER.info("upload processing date: [{}], {}", processingDate, processingDate.getDayOfWeek());
            if(DateUtils.isTradingOnHoliday(processingDate)) {
                uploadForDate(processingDate);
            } else if (DateUtils.isWeekend(processingDate)) {
                continue;
            } else {
                uploadForDate(processingDate);
            }
        } while(today.compareTo(processingDate) > 0);
    }
}
