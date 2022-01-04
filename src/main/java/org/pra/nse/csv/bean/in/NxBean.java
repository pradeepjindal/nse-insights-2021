package org.pra.nse.csv.bean.in;

import java.util.Date;
import java.util.Objects;

public class NxBean {
    private String symbol;
    private String idxName;
    private Date tradeDate;
    private double open;
    private double high;
    private double low;
    private double close;
    private double PointsChgAbs;
    private double PointsChgPct;
    private long volume;
    private double turnOverInCrore;
    private double pe;
    private double pb;
    private double divYield;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NxBean idxBean = (NxBean) o;
        return Objects.equals(idxName, idxBean.idxName) && Objects.equals(tradeDate, idxBean.tradeDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idxName, tradeDate);
    }


    @Override
    public String toString() {
        return "IdxBean{" +
                "idxName='" + idxName + '\'' +
                ", tradeDate=" + tradeDate +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", PointsChgAbs=" + PointsChgAbs +
                ", PointsChgPct=" + PointsChgPct +
                ", volume=" + volume +
                ", turnOverInCrore=" + turnOverInCrore +
                ", pe=" + pe +
                ", pb=" + pb +
                ", divYield=" + divYield +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIdxName() {
        return idxName;
    }

    public void setIdxName(String idxName) {
        this.idxName = idxName;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
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

    public double getPointsChgAbs() {
        return PointsChgAbs;
    }

    public void setPointsChgAbs(double pointsChgAbs) {
        PointsChgAbs = pointsChgAbs;
    }

    public double getPointsChgPct() {
        return PointsChgPct;
    }

    public void setPointsChgPct(double pointsChgPct) {
        PointsChgPct = pointsChgPct;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getTurnOverInCrore() {
        return turnOverInCrore;
    }

    public void setTurnOverInCrore(double turnOverInCrore) {
        this.turnOverInCrore = turnOverInCrore;
    }

    public double getPe() {
        return pe;
    }

    public void setPe(double pe) {
        this.pe = pe;
    }

    public double getPb() {
        return pb;
    }

    public void setPb(double pb) {
        this.pb = pb;
    }

    public double getDivYield() {
        return divYield;
    }

    public void setDivYield(double divYield) {
        this.divYield = divYield;
    }
}
