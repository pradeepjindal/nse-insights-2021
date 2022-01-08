package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
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
        if(ApCo.MANAGER_CALC_UPLOAD_ENABLED) {
            LOGGER.info("____________________ CALC Upload Manager");
        } else {
            LOGGER.info("____________________ CALC Upload Manager - DISABLED");
            return;
        }

        LOGGER.info("----------");
        calcAvgUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        calcRsiUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        calcMfiUploader.uploadFromDefaultDate();

//        LOGGER.info("----------");
//        calcAvgUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        calcRsiUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        calcMfiUploader.uploadFromLatestDate();

//        LOGGER.info("----------");
//        calcAvgUploader.uploadFromDate(LocalDate.of(2020,8,21));
//        LOGGER.info("----------");
//        calcRsiUploader.uploadFromDate(LocalDate.of(2020,8,21));
//        LOGGER.info("----------");
//        calcMfiUploader.uploadFromDate(LocalDate.of(2020,8,21));


        LOGGER.info("======================================== CALC - Upload Manager");
    }

}
