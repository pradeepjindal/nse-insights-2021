package org.pra.nse.statistics;

import org.pra.nse.Manager;
import org.pra.nse.db.dto.DeliverySpikeDto;
import org.pra.nse.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * polymorphism is not letting me share the data in base class
 * inheritance letting me share the common code, constants but not the date from base
 * in any case i have to make 3 objects means 3 copies of data means data duplication
 * if i want to keep the single copy of data, it seems only composition is the way to go
 * what i mean to say, i can not do this:
 * > created the object maruti and filled the petrol
 * > run maruti a little, now i want this to be transformed into wagonR (it wont transform, that is another issue)
 * oops! i can not do that, i have to create a separate wagonR and fill the petrol again
 * there is not way i can use maruti petrol in wagonR while both are child of same FourWheeler class
 * only composition is a way to do so (by oop it can not be achieved)
 *
 * oop do polymorphism (via different instances)
 * composition can do transformation (can operate different instances on the same data)
 *
 * FourWheeler -> Maruti
 * FourWheeler -> WagonR
 *
 * Morpheus :: Maruti :: WagonR
 * Morpheus(Maruit)
 * Morpheus.fillPetrol
 * Morpheus.run
 * Morpheus.transform(WagonR)
 * Morpheus.run
 */

@Component
public class StatisticsManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsManager.class);

    private final DataService dataService;
    private final Statisian statisian;

    public StatisticsManager(DataService dataService, Statisian statisian) {
        this.dataService = dataService;
        this.statisian = statisian;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ Statistics Manager");

        //statisian.stats(LocalDate.of(2020, 2, 5), 10);

        LocalDate tdy = LocalDate.of(2020, 3, 5);
        Map<String, List<DeliverySpikeDto>> sbiMap;

//        //delivery
//        LOGGER.info("SBIN (delivery-FALL) - ");
//        sbiMap = dataService.getRawDataBySymbol(LocalDate.of(2019,10,9), LocalDate.of(2019,7, 17),"SBIN");
//        StatisticsSbi.summarizeDelivery(sbiMap);
//        //delivery
//        LOGGER.info("SBIN (delivery-RISE) - ");
//        sbiMap = dataService.getRawDataBySymbol(LocalDate.of(2019,11,28), LocalDate.of(2019,10,9),"SBIN");
//        StatisticsSbi.summarizeDelivery(sbiMap);

        //delivery
        LOGGER.info("SBIN (delivery-FALL) - ");
        sbiMap = dataService.getRawDataBySymbol(LocalDate.of(2020,2,3), LocalDate.of(2020,1, 24),"SBIN");
        StatisticsSbi.summarizeDelivery(sbiMap);
        //delivery
        LOGGER.info("SBIN (delivery-RISE) - ");
        sbiMap = dataService.getRawDataBySymbol(LocalDate.of(2020,2, 12), LocalDate.of(2020,2,3),"SBIN");
        StatisticsSbi.summarizeDelivery(sbiMap);


        //delivery
        LOGGER.info("SBIN (delivery-FALL) - ");
        sbiMap = dataService.getRawDataBySymbol(LocalDate.of(2020,3, 9), LocalDate.of(2020,2,27),"SBIN");
        StatisticsSbi.summarizeDelivery(sbiMap);


//        //3yr
//        LOGGER.info("SBIN (3yr) - ");
//        sbiMap = dataService.getRawDataBySymbol(tdy, 750, "SBIN");
//        StatisticsSbi.summarizeBucket(sbiMap);
//
//        //2yr
//        LOGGER.info("SBIN (2yr) - ");
//        sbiMap = dataService.getRawDataBySymbol(tdy, 500, "SBIN");
//        StatisticsSbi.summarizeBucket(sbiMap);
//
//        //1yr
//        LOGGER.info("SBIN (1yr) - ");
//        sbiMap = dataService.getRawDataBySymbol(tdy, 250, "SBIN");
//        StatisticsSbi.summarizeBucket(sbiMap);
//
//        //6mo
//        LOGGER.info("SBIN (6mo) - ");
//        sbiMap = dataService.getRawDataBySymbol(tdy, 123, "SBIN");
//        StatisticsSbi.summarizeBucket(sbiMap);
//
//        //3mo
//        LOGGER.info("SBIN (3mo) - ");
//        sbiMap = dataService.getRawDataBySymbol(tdy, 66, "SBIN");
//        StatisticsSbi.summarizeBucket(sbiMap);

        LOGGER.info("======================================== Statistics Manager");
    }

}
