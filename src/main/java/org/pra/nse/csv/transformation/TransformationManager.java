package org.pra.nse.csv.transformation;

import org.pra.nse.ApCo;
import org.pra.nse.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class TransformationManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationManager.class);

    private final CmTransformer cmTransformer;
    private final FmTransformer fmTransformer;
    private final DmTransformer dmTransformer;
    private final AbTransformer abTransformer;
    private final NxTransformer nxTransformer;
    private final FoTransformer foTransformer;

    public TransformationManager(CmTransformer cmTransformer,
                                 FmTransformer fmTransformer,
                                 DmTransformer dmTransformer,
                                 AbTransformer abTransformer,
                                 NxTransformer nxTransformer,
                                 FoTransformer foTransformer) {
        this.cmTransformer = cmTransformer;
        this.fmTransformer = fmTransformer;
        this.dmTransformer = dmTransformer;
        this.abTransformer = abTransformer;
        this.nxTransformer = nxTransformer;
        this.foTransformer = foTransformer;
    }


    @Override
    public void execute() {
//        LOGGER.info(".");
        if(ApCo.MANAGER_TRANSFORM_ENABLED) {
            LOGGER.info("____________________ Transform Manager");
        } else {
            LOGGER.info("____________________ Transform Manager - DISABLED");
            return;
        }

        LOGGER.info("----------");
        cmTransformer.transformFromDefaultDate();
        LOGGER.info("----------");
        dmTransformer.transformFromDefaultDate();
        LOGGER.info("----------");
        fmTransformer.transformFromDefaultDate();
        LOGGER.info("----------");
        nxTransformer.transformFromDefaultDate();
        LOGGER.info("----------");
        abTransformer.transformFromDefaultDate();
        LOGGER.info("----------");
        foTransformer.transformFromDefaultDate();

//        cmTransformer.transformFromLatestDate();
//        LOGGER.info("----------");
//        abTransformer.transformFromLatestDate();
//        LOGGER.info("----------");
//        fmTransformer.transformFromLatestDate();
//        LOGGER.info("----------");
//        nxTransformer.transformFromLatestDate();
//        LOGGER.info("----------");
//        dmTransformer.transformFromLatestDate();
//        LOGGER.info("----------");
//        foTransformer.transformFromLatestDate();

//        cmTransformer.transformFromDate(LocalDate.of(2016,1,1));
//        LOGGER.info("----------");
//        dmTransformer.transformFromDate(LocalDate.of(2016,1,1));
//        LOGGER.info("----------");
//        nxTransformer.transformFromDate(LocalDate.of(2020,1,1));

        LOGGER.info("======================================== Transform Manager");
    }

}
