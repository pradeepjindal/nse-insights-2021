package org.pra.nse.csv.data;

import org.pra.nse.ApCo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CAO Csv Access Object
 */
public class RsiCaoNew {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsiCaoNew.class);


    public static void saveOverWrite(String csvHeaderString,
                                     List<CalcBeanNew> beans,
                                     String toPath,
                                     Function<CalcBeanNew, String> csvStringFunction) {
        // create and collect csv lines
        List<String> csvLines = new ArrayList<>();
        //symbolMap.values().forEach( list -> list.forEach( dto -> csvLines.add(dto.toFullCsvString())));
        beans.forEach( bean -> csvLines.add( csvStringFunction.apply(bean) ));

        // print csv lines
        File csvOutputFile = new File(toPath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(csvHeaderString);
            csvLines.stream()
                    //.map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error: {}", e);
            throw new RuntimeException("RSI | could not create file: " + toPath);
        }
    }

    public static void saveAppend(List<RsiBeanNew> dtos) {

        //List<RsiEntity> rsiBeans0 = RsiData.load();
        String toPath = ApCo.ROOT_DIR + File.separator + ApCo.COMPUTE_DIR_NAME + File.separator + "rsi.csv";
        //
//        List<RsiBean> rsiBeans = new ArrayList<>();
//        dtoHavingRsi.stream().forEach( dto -> {
//            RsiBean rsiBean = new RsiBean();
//            rsiBean.setSymbol(dto.getSymbol());
//            //rsiBean.setTradeDate(DateUtils.toUtilDate(dto.getTradeDate()));
//            rsiBean.setTradeDate(dto.getTradeDate());
//            rsiBean.setCloseRsi10Ema(dto.getTdyCloseRsi10Ema());
//            rsiBean.setLastRsi10Ema(dto.getTdyLastRsi10Ema());
//            rsiBean.setAtpRsi10Ema(dto.getTdyAtpRsi10Ema());
//            rsiBeans.add(rsiBean);
//        });

        // create and collect csv lines
        List<String> csvLines = new ArrayList<>();
        dtos.forEach( bean -> csvLines.add(bean.toCsvString()));

        // print csv lines
        File csvOutputFile = new File(toPath);
        //overwrite mode
        //try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
        //append mode
        try (FileWriter fw = new FileWriter(csvOutputFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println("symbol, trade_date, OpenRsi10Ema, HighRsi10Ema, LowRsi10Ema, CloseRsi10Ema, LastRsi10Ema, AtpRsi10Ema, HighLowMidRsi10Ema");
            csvLines.stream()
                    //.map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (IOException e) {
            LOGGER.error("Error: {}", e);
            throw new RuntimeException("rsi: Could not create file");
        }
    }

}
