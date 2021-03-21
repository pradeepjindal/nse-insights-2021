package org.pra.nse.report;

public class IndicatorUtils {
    public static String calculateHammer(float open, float high, float low, float close) {
        double highLow = high - low;
        double openClose = open - close;
        double openLow = open - low;
        double closeLow = close - low;
        if(highLow > (3 * openClose)
                && closeLow / (0.001 + highLow) > 0.6
                && openLow / (0.001 + highLow) > 0.6
        ) {
            //LOGGER.info("Hammer Detected: {}", cmBean.getSymbol());
            return "Hammer";
        } else {
            //LOGGER.info("Hammer         : {}", cmBean.getSymbol());
            return "";
        }
    }
}
