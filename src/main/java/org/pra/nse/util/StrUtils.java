package org.pra.nse.util;

public class StrUtils {
    public static String replaceLast(String string, String replaceThis, String withThis) {
        int pos = string.lastIndexOf(replaceThis);
        if (pos > -1) {
            return string.substring(0, pos)
                    + withThis
                    + string.substring(pos + replaceThis.length());
        } else {
            return string;
        }
    }
}
