package org.pra.nse.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MathUtil {

    public static double median(double[] array) {
        Arrays.sort(array);
        if(array.length % 2 == 0) {
            int half = array.length / 2;
            double first = array[half];
            double second = array[++half];
            return (first+second)/2;
        } else {
            int half = array.length / 2;
            half++;
            return array[half];
        }
    }

    long gcd(long a, long b)
    {
        // Everything divides 0
        if (a == 0)
            return b;
        if (b == 0)
            return a;

        // base case
        if (a == b)
            return a;

        // a is greater
        if (a > b)
            return gcd(a-b, b);
        return gcd(a, b-a);
    }

}
