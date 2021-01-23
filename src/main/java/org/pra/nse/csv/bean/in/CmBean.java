package org.pra.nse.csv.bean.in;

import java.util.Date;
import java.util.Objects;

public class CmBean {

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
    private Date timestamp;
    private long totalTrades;
    private String isin;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CmBean cmBean = (CmBean) o;
        return symbol.equals(cmBean.symbol) &&
                series.equals(cmBean.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, series);
    }

    @Override
    public String toString() {
        return "CmBean{" +
                "symbol='" + symbol + '\'' +
                ", series='" + series + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", last=" + last +
                ", prevClose=" + prevClose +
                ", totTrdQty=" + totTrdQty +
                ", totTrdVal=" + totTrdVal +
                ", timestamp=" + timestamp +
                ", totalTrades=" + totalTrades +
                ", isin='" + isin + '\'' +
                '}';
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
