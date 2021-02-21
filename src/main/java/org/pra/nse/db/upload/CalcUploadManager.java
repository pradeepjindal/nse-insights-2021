package org.pra.nse.db.upload;

import org.pra.nse.Manager;
import org.pra.nse.db.upload.calc.CalcAvgUploader;
import org.pra.nse.db.upload.calc.CalcMfiUploader;
import org.pra.nse.db.upload.calc.CalcRsiUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CalcUploadManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalcUploadManager.class);

    private final CalcAvgUploader calcAvgUploader;
    private final CalcMfiUploader calcMfiUploader;
    private final CalcRsiUploader calcRsiUploader;

    public CalcUploadManager(CalcAvgUploader calcAvgUploader,
                             CalcMfiUploader calcMfiUploader,
                             CalcRsiUploader calcRsiUploader) {
        this.calcAvgUploader = calcAvgUploader;
        this.calcMfiUploader = calcMfiUploader;
        this.calcRsiUploader = calcRsiUploader;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ CALC - Upload Manager");

        LOGGER.info("----------");
        calcAvgUploader.uploadFromLatestDate();
        LOGGER.info("----------");
        calcMfiUploader.uploadFromLatestDate();
        LOGGER.info("----------");
        calcRsiUploader.uploadFromLatestDate();

//        LOGGER.info("----------");
//        calcAvgUploader.uploadFromDefaultDate();
//        LOGGER.info("----------");
//        calcMfiUploader.uploadFromDefaultDate();
//        LOGGER.info("----------");
//        calcRsiUploader.uploadFromDefaultDate();

//        LOGGER.info("----------");
//        calcAvgUploader.uploadFromDate(ApCo.UPLOAD_CALC_FROM_DATE_NEW);
//        LOGGER.info("----------");
//        calcMfiUploader.uploadFromDate(ApCo.UPLOAD_CALC_FROM_DATE_NEW);
//        LOGGER.info("----------");
//        calcRsiUploader.uploadFromDate(ApCo.UPLOAD_CALC_FROM_DATE_NEW);

        LOGGER.info("======================================== CALC - Upload Manager");
    }

}
