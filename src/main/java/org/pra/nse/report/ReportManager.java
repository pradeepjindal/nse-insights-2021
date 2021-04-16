package org.pra.nse.report;

import org.pra.nse.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
public class ReportManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportManager.class);

    private final PastPresentFutureReporter pastPresentFutureReporter;

    public ReportManager(PastPresentFutureReporter pastPresentFutureReporter) {
        this.pastPresentFutureReporter = pastPresentFutureReporter;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ Report Manager");

        //pastPresentFutureReporterNew.reportFromLast(2);
//        pastPresentFutureReporterNew.reportFromLast(3);
//        pastPresentFutureReporterNew.reportFromLast(5);

//        pastPresentFutureReporter.reportFromLast(10);
//        pastPresentFutureReporter.reportFromLast(15);
        pastPresentFutureReporter.reportFromLast(40);

//        pastPresentFutureReporter.reportForDate(LocalDate.of(2021,3,30),20);

        LOGGER.info("======================================== Report Manager");
    }

}
