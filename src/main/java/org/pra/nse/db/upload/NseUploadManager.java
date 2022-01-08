package org.pra.nse.db.upload;

import org.pra.nse.ApCo;
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
    private final NseOmUploader nseOmUploader;
    private final NseDmUploader nseDmUploader;
    private final NseNxUploader nseNxUploader;
    private final NseLsUploader nseLsUploader;
    private final NseFoUploader nseFoUploader;

    public NseUploadManager(NseCmUploader nseCmUploader,
                            NseFmUploader nseFmUploader,
                            NseOmUploader nseOmUploader,
                            NseDmUploader nseDmUploader,
                            NseNxUploader nseNxUploader,
                            NseLsUploader nseLsUploader,
                            NseFoUploader nseFoUploader) {
        this.nseCmUploader = nseCmUploader;
        this.nseFmUploader = nseFmUploader;
        this.nseOmUploader = nseOmUploader;
        this.nseDmUploader = nseDmUploader;
        this.nseNxUploader = nseNxUploader;
        this.nseLsUploader = nseLsUploader;
        this.nseFoUploader = nseFoUploader;
    }


    @Override
    public void execute() {
//        LOGGER.info(".");
        if(ApCo.MANAGER_NSE_UPLOAD_ENABLED) {
            LOGGER.info("____________________ NSE Upload Manager");
        } else {
            LOGGER.info("____________________ NSE Upload Manager - DISABLED");
            return;
        }

//        nseCmUploader.upload2021();
//        nseCmUploader.uploadForDate(LocalDate.of(2020, 11, 23));
//        LOGGER.info("----------");
//        nseDmUploader.upload2021();
//        LOGGER.info("----------");
//        nseFmUploader.upload2021();
//        LOGGER.info("----------");
//        nseOmUploader.upload2021();
//        LOGGER.info("----------");
//        nseNxUploader.upload2021();


        nseCmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseDmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseFmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseOmUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseNxUploader.uploadFromDefaultDate();
        LOGGER.info("----------");
        nseFoUploader.uploadFromDefaultDate();


//        nseCmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseDmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseFmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseNxUploader.uploadFromLatestDate();


//        LOGGER.info("----------");
//        nseDmUploader.uploadFromDate(LocalDate.of(2017,1,1));
//        LOGGER.info("----------");
//        nseNxUploader.uploadFromDate(LocalDate.of(2020,1,1));


        //        LOGGER.info("----------");
//        nseCmUploader.uploadForDate(LocalDate.of(2017,7,10));

        LOGGER.info("======================================== NSE - Upload Manager");
    }

}
