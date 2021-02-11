package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
import org.pra.nse.Manager;
import org.pra.nse.db.upload.calc.CalcAvgUploaderNew;
import org.pra.nse.db.upload.calc.CalcMfiUploaderNew;
import org.pra.nse.db.upload.calc.CalcRsiUploaderNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CalcUploadManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalcUploadManager.class);

    private final CalcAvgUploaderNew calcAvgUploaderNew;
    private final CalcMfiUploaderNew calcMfiUploaderNew;
    private final CalcRsiUploaderNew calcRsiUploaderNew;

    public CalcUploadManager(CalcAvgUploaderNew calcAvgUploaderNew,
                             CalcMfiUploaderNew calcMfiUploaderNew,
                             CalcRsiUploaderNew calcRsiUploaderNew) {
        this.calcAvgUploaderNew = calcAvgUploaderNew;
        this.calcMfiUploaderNew = calcMfiUploaderNew;
        this.calcRsiUploaderNew = calcRsiUploaderNew;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ CALC - Upload Manager");

//        LOGGER.info("----------");
//        calcAvgUploaderNew.uploadFromLastDate();
//        LOGGER.info("----------");
//        calcMfiUploaderNew.uploadFromLastDate();
//        LOGGER.info("----------");
//        calcRsiUploaderNew.uploadFromLastDate();

        LOGGER.info("----------");
//        calcAvgUploaderNew.uploadFromDate(ApCo.UPLOAD_CALC_FROM_DATE);
        LOGGER.info("----------");
//        calcMfiUploaderNew.uploadFromDate(ApCo.UPLOAD_CALC_FROM_DATE);
        LOGGER.info("----------");
//        calcRsiUploaderNew.uploadFromDate(ApCo.UPLOAD_CALC_FROM_DATE);

        LOGGER.info("======================================== CALC - Upload Manager");
    }

}
