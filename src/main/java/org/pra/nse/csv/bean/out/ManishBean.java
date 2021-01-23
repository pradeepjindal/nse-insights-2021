package org.pra.nse.csv.bean.out;

import java.time.LocalDate;
import java.util.Date;

public class ManishBean {
    private String symbol;
    private Date expiryDate;
    private LocalDate expiryLocalDate;
    private Date tdyDate;
    //
    private double cmOpen;
    private double cmHigh;
    private double cmLow;
    private double cmClose;
    private double tdyClose;
    private long tdyTraded;
    private long tdyDelivery;
    private double deliveryToTradeRatio;
    private long tdyOI;
    private double fuClose;
    private long oiOne;
    private long oiTwo;
    private long oiThree;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getTdyDate() {
        return tdyDate;
    }

    public void setTdyDate(Date tdyDate) {
        this.tdyDate = tdyDate;
    }

    public LocalDate getExpiryLocalDate() {
        return expiryLocalDate;
    }

    public void setExpiryLocalDate(LocalDate expiryLocalDate) {
        this.expiryLocalDate = expiryLocalDate;
    }

    public double getCmOpen() {
        return cmOpen;
    }

    public void setCmOpen(double cmOpen) {
        this.cmOpen = cmOpen;
    }

    public double getCmHigh() {
        return cmHigh;
    }

    public void setCmHigh(double cmHigh) {
        this.cmHigh = cmHigh;
    }

    public double getCmLow() {
        return cmLow;
    }

    public void setCmLow(double cmLow) {
        this.cmLow = cmLow;
    }

    public double getCmClose() {
        return cmClose;
    }

    public void setCmClose(double cmClose) {
        this.cmClose = cmClose;
    }

    public double getTdyClose() {
        return tdyClose;
    }

    public void setTdyClose(double tdyClose) {
        this.tdyClose = tdyClose;
    }

    public long getTdyTraded() {
        return tdyTraded;
    }

    public void setTdyTraded(long tdyTraded) {
        this.tdyTraded = tdyTraded;
    }

    public long getTdyDelivery() {
        return tdyDelivery;
    }

    public void setTdyDelivery(long tdyDelivery) {
        this.tdyDelivery = tdyDelivery;
    }

    public double getDeliveryToTradeRatio() {
        return deliveryToTradeRatio;
    }

    public void setDeliveryToTradeRatio(double deliveryToTradeRatio) {
        this.deliveryToTradeRatio = deliveryToTradeRatio;
    }

    public double getFuClose() {
        return fuClose;
    }

    public void setFuClose(double fuClose) {
        this.fuClose = fuClose;
    }

    public long getTdyOI() {
        return tdyOI;
    }

    public void setTdyOI(long tdyOI) {
        this.tdyOI = tdyOI;
    }

    public long getOiOne() {
        return oiOne;
    }

    public void setOiOne(long oiOne) {
        this.oiOne = oiOne;
    }

    public long getOiTwo() {
        return oiTwo;
    }

    public void setOiTwo(long oiTwo) {
        this.oiTwo = oiTwo;
    }

    public long getOiThree() {
        return oiThree;
    }

    public void setOiThree(long oiThree) {
        this.oiThree = oiThree;
    }
}
