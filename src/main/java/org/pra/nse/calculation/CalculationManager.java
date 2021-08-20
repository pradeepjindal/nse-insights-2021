package org.pra.nse.calculation;

import org.pra.nse.ApCo;
import org.pra.nse.Manager;
import org.pra.nse.util.NseFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.pra.nse.calculation.CalcCons.AVG_FILE_PREFIX;
import static org.pra.nse.calculation.CalcCons.MFI_FILE_PREFIX;
import static org.pra.nse.calculation.CalcCons.RSI_FILE_PREFIX;

@Component
public class CalculationManager implements Manager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationManager.class);

    private final NseFileUtils nseFileUtils;

    private final AvgCalculator avgCalculator;
    private final MfiCalculator mfiCalculator;
    private final RsiCalculator rsiCalculator;


    public CalculationManager(NseFileUtils nseFileUtils,
                              AvgCalculator avgCalculator,
                              MfiCalculator mfiCalculator,
                              RsiCalculator rsiCalculator) {
        this.nseFileUtils = nseFileUtils;
        this.avgCalculator = avgCalculator;
        this.mfiCalculator = mfiCalculator;
        this.rsiCalculator = rsiCalculator;
    }

    @Override
    public void execute() {
        LOGGER.info(".");
        LOGGER.info("____________________ Calculation Manager");

//        rsiCalculator.calculateAndReturn(LocalDate.of(2021, 7, 23), 20, "ABFRL");
//        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        return;

        LOGGER.info("----------");
        nseFileUtils.getDatesToBeComputed(()-> AVG_FILE_PREFIX, CalcCons.AVG_DIR_NAME, ApCo.DO_CALC_FROM_DATE)
                .forEach( forDate -> {
                    LOGGER.info(".");
                    LOGGER.info("calc-{} | for:{}", AVG_FILE_PREFIX, forDate.toString());
                    try {
                        avgCalculator.calculateAndSave(forDate);
                    } catch (Exception e) {
                        LOGGER.error("ERROR:", e);
                    }
                });

        LOGGER.info("----------");
        nseFileUtils.getDatesToBeComputed(()-> MFI_FILE_PREFIX, CalcCons.MFI_DIR_NAME, ApCo.DO_CALC_FROM_DATE)
                .forEach( forDate -> {
                    LOGGER.info(".");
                    LOGGER.info("calc-{} | for:{}", MFI_FILE_PREFIX, forDate.toString());
                    try {
                        mfiCalculator.calculateAndSave(forDate);
                    } catch (Exception e) {
                        LOGGER.error("ERROR:", e);
                    }
                });

        LOGGER.info("----------");
        nseFileUtils.getDatesToBeComputed(()-> RSI_FILE_PREFIX, CalcCons.RSI_DIR_NAME, ApCo.DO_CALC_FROM_DATE)
                .forEach( forDate -> {
                    LOGGER.info(".");
                    LOGGER.info("calc-{} | for:{}", RSI_FILE_PREFIX, forDate.toString());
                    try {
                        rsiCalculator.calculateAndSave(forDate);
                    } catch (Exception e) {
                        LOGGER.error("ERROR:", e);
                    }
                });

        LOGGER.info("======================================== Calculation Manager");
    }
}
