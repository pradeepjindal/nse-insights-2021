package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "Nse_Cash_Market_Tab")
public class NseCashMarketTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @SequenceGenerator(name = "nse_cash_market_seq_id", sequenceName = "nse_cash_market_seq_id", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nse_cash_market_seq_id")
    private Long id;

    private String symbol;
    private String series;
    private double open;
    private double high;
    private double low;
    private double close;
    private double last;
    private double prevClose;
    private long totTrdQty;
    private double totTrdVal;
    private LocalDate tradeDate;
    private long totalTrades;
    private String isin;

//    @Id
//    @GeneratedValue(generator = "question_generator")
//    @SequenceGenerator(
//            name = "question_generator",
//            sequenceName = "question_sequence",
//            initialValue = 1000
//    )


    public void reset() {
        id = null;
        symbol = null;
        series = null;
        open = 0;
        high = 0;
        low = 0;
        close = 0;
        last = 0;
        prevClose = 0;
        totTrdQty = 0;
        totTrdVal = 0;
        tradeDate = null;
        totalTrades = 0;
        isin = null;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getPrevClose() {
        return prevClose;
    }

    public void setPrevClose(double prevClose) {
        this.prevClose = prevClose;
    }

    public long getTotTrdQty() {
        return totTrdQty;
    }

    public void setTotTrdQty(long totTrdQty) {
        this.totTrdQty = totTrdQty;
    }

    public double getTotTrdVal() {
        return totTrdVal;
    }

    public void setTotTrdVal(double totTrdVal) {
        this.totTrdVal = totTrdVal;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public long getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(long totalTrades) {
        this.totalTrades = totalTrades;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }
}
