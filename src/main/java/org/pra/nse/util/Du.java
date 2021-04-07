package org.pra.nse.util;

//DataUtil
public class Du {
    public static String symbol(String symbol) {
        int width = 10;
        char fill = ' ';

        String toPad = symbol; //"Net York";
        String padded = new String(new char[width - toPad.length()]).replace('\0', fill) + toPad;
//        System.out.println(padded);
        return padded;
    }
}
