package org.pra.nse.csv.bean.in;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class EqnBean {
    private String symbol;
    private String series;
    @JsonFormat(pattern="dd-MMM-yyyy")
    private Date date;

    private double prevClose;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double lastPrice;
    private double closePrice;
    private double averagePrice;

    private long totalTradedQuantity;
    private double turnOver;
    private long noOfTrades;
    private long deliverableQty;

    private double dlyQttoTradedQty;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrevClose() {
        return prevClose;
    }

    public void setPrevClose(double prevClose) {
        this.prevClose = prevClose;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public long getTotalTradedQuantity() {
        return totalTradedQuantity;
    }

    public void setTotalTradedQuantity(long totalTradedQuantity) {
        this.totalTradedQuantity = totalTradedQuantity;
    }

    public double getTurnOver() {
        return turnOver;
    }

    public void setTurnOver(double turnOver) {
        this.turnOver = turnOver;
    }

    public long getNoOfTrades() {
        return noOfTrades;
    }

    public void setNoOfTrades(long noOfTrades) {
        this.noOfTrades = noOfTrades;
    }

    public long getDeliverableQty() {
        return deliverableQty;
    }

    public void setDeliverableQty(long deliverableQty) {
        this.deliverableQty = deliverableQty;
    }

    public double getDlyQttoTradedQty() {
        return dlyQttoTradedQty;
    }

    public void setDlyQttoTradedQty(double dlyQttoTradedQty) {
        this.dlyQttoTradedQty = dlyQttoTradedQty;
    }
}
