package org.pra.nse.db.upload;

import org.pra.nse.Manager;
import org.pra.nse.db.upload.nse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NseUploadManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseUploadManager.class);

    private final NseCmUploader nseCmUploader;
    private final NseFmUploader nseFmUploader;
    private final NseDmUploader nseDmUploader;
    private final NseIdxUploader nseIdxUploader;

    public NseUploadManager(NseCmUploader nseCmUploader,
                            NseFmUploader nseFmUploader,
                            NseDmUploader nseDmUploader,
                            NseIdxUploader nseIdxUploader) {
        this.nseCmUploader = nseCmUploader;
        this.nseFmUploader = nseFmUploader;
        this.nseDmUploader = nseDmUploader;
        this.nseIdxUploader = nseIdxUploader;
    }


    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ NSE - Upload Manager");

        nseCmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseDmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseFmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseIdxUploader.uploadFromDefaultDate();

//        nseCmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseDmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseFmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseIdxUploader.uploadFromLatestDate();

//        LOGGER.info("----------");
//        nseDmUploader.uploadFromDate(LocalDate.of(2017,1,1));
//        LOGGER.info("----------");
//        nseIdxUploader.uploadFromDate(LocalDate.of(2020,1,1));


        //        LOGGER.info("----------");
//        nseCmUploader.uploadForDate(LocalDate.of(2020,8,28));

        LOGGER.info("======================================== NSE - Upload Manager");
    }

}
