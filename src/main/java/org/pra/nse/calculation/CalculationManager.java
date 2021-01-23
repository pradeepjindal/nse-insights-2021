package org.pra.nse.calculation;

import org.pra.nse.ApCo;
import org.pra.nse.Manager;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.pra.nse.calculation.CalcCons.AVG_FILE_PREFIX;
import static org.pra.nse.calculation.CalcCons.MFI_FILE_PREFIX;
import static org.pra.nse.calculation.CalcCons.RSI_FILE_PREFIX;

@Component
public class CalculationManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationManager.class);

    private final NseFileUtils nseFileUtils;

    private final AvgCalculatorNew avgCalculatorNew;
    private final MfiCalculatorNew mfiCalculatorNew;
    private final RsiCalculatorNew rsiCalculatorNew;


    public CalculationManager(NseFileUtils nseFileUtils,
                              AvgCalculatorNew avgCalculatorNew,
                              MfiCalculatorNew mfiCalculatorNew,
                              RsiCalculatorNew rsiCalculatorNew) {
        this.nseFileUtils = nseFileUtils;
        this.avgCalculatorNew = avgCalculatorNew;
        this.mfiCalculatorNew = mfiCalculatorNew;
        this.rsiCalculatorNew = rsiCalculatorNew;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ Calculation Manager");

        //rsiCalculator.calculateAndReturn(LocalDate.of(2020,02,10));
        //rsiCalculator.calculateAndReturn(LocalDate.of(2020,02,10), "BAJAJ-AUTO");

        //mfiCalculatorNew.calculateAndReturn(LocalDate.of(2020,02,14));
        //mfiCalculatorNew.calculateAndSave(LocalDate.of(2020,2,14));

        LOGGER.info("----------");
        nseFileUtils.getDatesToBeComputed(()-> AVG_FILE_PREFIX, CalcCons.AVG_DIR_NAME_NEW, ApCo.CALC_FROM_DATE_NEW)
                .forEach( forDate -> {
                    LOGGER.info(".");
                    LOGGER.info("calc-{} | for:{}", AVG_FILE_PREFIX, forDate.toString());
                    try {
                        avgCalculatorNew.calculateAndSave(forDate);
                    } catch (Exception e) {
                        LOGGER.error("ERROR: {}", e);
                    }
                });

        LOGGER.info("----------");
        nseFileUtils.getDatesToBeComputed(()-> MFI_FILE_PREFIX, CalcCons.MFI_DIR_NAME_NEW, ApCo.CALC_FROM_DATE_NEW)
                .forEach( forDate -> {
                    LOGGER.info(".");
                    LOGGER.info("calc-{} | for:{}", MFI_FILE_PREFIX, forDate.toString());
                    try {
                        mfiCalculatorNew.calculateAndSave(forDate);
                    } catch (Exception e) {
                        LOGGER.error("ERROR: {}", e);
                    }
                });

        LOGGER.info("----------");
        nseFileUtils.getDatesToBeComputed(()-> RSI_FILE_PREFIX, CalcCons.RSI_DIR_NAME_NEW, ApCo.CALC_FROM_DATE_NEW)
                .forEach( forDate -> {
                    LOGGER.info(".");
                    LOGGER.info("calc-{} | for:{}", RSI_FILE_PREFIX, forDate.toString());
                    try {
                        rsiCalculatorNew.calculateAndSave(forDate);
                    } catch (Exception e) {
                        LOGGER.error("ERROR: {}", e);
                    }
                });

        LOGGER.info("======================================== Calculation Manager");
    }
}
