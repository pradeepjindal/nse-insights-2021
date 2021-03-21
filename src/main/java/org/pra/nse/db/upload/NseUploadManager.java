package org.pra.nse.db.upload;

import org.pra.nse.Manager;
import org.pra.nse.db.upload.nse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NseUploadManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NseUploadManager.class);

    private final NseCmUploaderNew nseCmUploader;
    private final NseFmUploaderNew nseFmUploader;
    private final NseDmUploaderNew nseDmUploader;
    private final NseIdxUploaderNew nseIdxUploader;

    public NseUploadManager(NseCmUploaderNew nseCmUploader,
                            NseFmUploaderNew nseFmUploader,
                            NseDmUploaderNew nseDmUploader,
                            NseIdxUploaderNew nseIdxUploader) {
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
//        LOGGER.info("----------");
//        nseIdxUploader.uploadFromDefaultDate();

//        nseCmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseDmUploader.uploadFromLatestDate();
//        LOGGER.info("----------");
//        nseFmUploader.uploadFromLatestDate();
        LOGGER.info("----------");
        nseIdxUploader.uploadFromLatestDate();


//        cashMarketUploader.uploadFromDate(LocalDate.of(2017,1,1));
//        LOGGER.info("----------");
//        deliveryMarketUploader.uploadFromDate(LocalDate.of(2017,1,1));
//        LOGGER.info("----------");
//        nseIdxUploader.uploadFromDate(LocalDate.of(2020,1,1));

        LOGGER.info("======================================== NSE - Upload Manager");
    }

}
