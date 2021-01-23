package org.pra.nse.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    public static final BigDecimal HUNDRED = new BigDecimal(100);

    public static BigDecimal onePercent(BigDecimal figure) {
        return figure == null ? BigDecimal.ZERO : divide(figure, HUNDRED);
    }

    public static BigDecimal divide(BigDecimal to, BigDecimal by) {
        return by.compareTo(BigDecimal.ZERO) == 0 ? to : to.divide(by, 2, RoundingMode.HALF_UP);
    }
}
