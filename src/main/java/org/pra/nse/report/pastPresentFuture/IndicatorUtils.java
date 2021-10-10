package org.pra.nse.report.pastPresentFuture;

public class IndicatorUtils {
    public static String calculateHammerManishVersion(float open, float high, float low, float close) {
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

    public static String calculateHammerPradeepVersion(float open, float high, float low, float close) {
        String hammer = "";
        double body = 0.1, higherSideWick = 0.1, lowerSideWick = 0.1;

        if(open == close) {
            body = 0.1;
            higherSideWick = high - open;
            lowerSideWick = open - low;
        }

        if(open > close) {
            body = open - close;
            higherSideWick = high - open;
            lowerSideWick = close - low;
        }

        if(close > open) {
            body = close - open;
            higherSideWick = high - close;
            lowerSideWick = open - low;
        }

        if(higherSideWick / body > 3) hammer = "dnHmr";
        if(lowerSideWick / body > 3) hammer = "upHmr";

        return hammer;
    }

}
