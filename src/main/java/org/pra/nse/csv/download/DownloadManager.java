package org.pra.nse.csv.download;

import org.pra.nse.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DownloadManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadManager.class);

    private final CmDownloader cmDownloader;
    private final FmDownloader fmDownloader;
    private final DmDownloader dmDownloader;
    private final BpDownloader bpDownloader;
    private final DbcDownloader dbcDownloader;
    private final EqDownloader eqDownloader;

    public DownloadManager(CmDownloader cmDownloader,
                           FmDownloader fmDownloader,
                           DmDownloader dmDownloader,
                           BpDownloader bpDownloader,
                           DbcDownloader dbcDownloader,
                           EqDownloader eqDownloader) {
        this.cmDownloader = cmDownloader;
        this.fmDownloader = fmDownloader;
        this.dmDownloader = dmDownloader;
        this.bpDownloader = bpDownloader;
        this.dbcDownloader = dbcDownloader;
        this.eqDownloader = eqDownloader;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ Download Manager");

        cmDownloader.downloadFromDefaultDate();
        LOGGER.info("----------");
        dmDownloader.downloadFromDefaultDate();
        LOGGER.info("----------");
        fmDownloader.downloadFromDefaultDate();
        LOGGER.info("----------");
//        bpDownloader.downloadFromDefaultDate();
//        LOGGER.info("----------");
//        dbcDownloader.downloadFromDefaultDate();

//        cmDownloader.downloadFromLastDate();
//        LOGGER.info("----------");
//        fmDownloader.downloadFromLastDate();
//        LOGGER.info("----------");
//        dmDownloader.downloadFromLastDate();
//        LOGGER.info("----------");
//        bpDownloader.downloadFromLastDate();
//        LOGGER.info("----------");
//        dbcDownloader.downloadFromLastDate();

//        cmDownloader.downloadFromDate(LocalDate.of(2015,1,1));
//        LOGGER.info("----------");
//        dmDownloader.downloadFromDate(LocalDate.of(2015,1,1));
//        LOGGER.info("----------");
//        dbcDownloader.downloadFromDate(LocalDate.of(2015,1,1));

        //eqDownloader.downloadBySymbol("sbin");

        LOGGER.info("======================================== Download Manager");
    }

}
