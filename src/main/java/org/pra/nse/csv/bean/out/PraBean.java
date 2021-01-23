package org.pra.nse.csv.bean.out;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class PraBean {
    private String instrument;
    private String symbol;
    private Date expiryDate;
    private LocalDate expiryLocalDate;
    private double strikePrice;
    private String optionType;
    //
    private long contracts;
    private String hammer;
    //
    private double cmTdyClose;
    private double foTdyClose;
    private long tdyOI;
    private long tdyDelivery;
    private Date tdyDate;
    private LocalDate tdyLocalDate;
    //
    private double cmPrevsClose;
    private double foPrevsClose;
    private long prevsOI;
    private long prevsDelivery;
    private Date prevsDate;
    private LocalDate prevsLocalDate;
    //
    private double cmPrcntChgInClose;
    private double foPrcntChgInClose;
    private double prcntChgInOI;
    private double prcntChgInDelivery;
    //
    private double cmOpen;
    private double cmHigh;
    private double cmLow;
    private double cmClose;
    private long cmTdyTraded;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PraBean praBean = (PraBean) o;
        return Double.compare(praBean.strikePrice, strikePrice) == 0 &&
                instrument.equals(praBean.instrument) &&
                symbol.equals(praBean.symbol) &&
                expiryDate.equals(praBean.expiryDate) &&
                optionType.equals(praBean.optionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, symbol, expiryDate, strikePrice, optionType);
    }

    @Override
    public String toString() {
        return "PraBean{" +
                "instrument='" + instrument + '\'' +
                ", symbol='" + symbol + '\'' +
                ", expiryDate=" + expiryDate +
                ", expiryLocalDate=" + expiryLocalDate +
                ", strikePrice=" + strikePrice +
                ", optionType='" + optionType + '\'' +
                ", contracts=" + contracts +
                ", hammer='" + hammer + '\'' +
                ", cmTdyClose=" + cmTdyClose +
                ", foTdyClose=" + foTdyClose +
                ", tdyOI=" + tdyOI +
                ", tdyDelivery=" + tdyDelivery +
                ", tdyDate=" + tdyDate +
                ", tdyLocalDate=" + tdyLocalDate +
                ", cmPrevsClose=" + cmPrevsClose +
                ", foPrevsClose=" + foPrevsClose +
                ", prevsOI=" + prevsOI +
                ", prevsDelivery=" + prevsDelivery +
                ", prevsDate=" + prevsDate +
                ", prevsLocalDate=" + prevsLocalDate +
                ", cmPrcntChgInClose=" + cmPrcntChgInClose +
                ", foPrcntChgInClose=" + foPrcntChgInClose +
                ", prcntChgInOI=" + prcntChgInOI +
                ", prcntChgInDelivery=" + prcntChgInDelivery +
                ", cmTdyTraded=" + cmTdyTraded +
                '}';
    }

    public String[] toArray() {
        String[] array = new String[5];
        array[0] = instrument;
        array[1] = symbol;
        array[2] = expiryDate.toString();
        array[3] = String.valueOf(strikePrice);
        array[4] = optionType;
        return array;
    }

    public String toCsvString() {
        return instrument
                + "," + symbol
                + "," + expiryDate.toString()
                + "," + strikePrice
                + "," + optionType;
    }


    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

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

    public double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public long getContracts() {
        return contracts;
    }

    public void setContracts(long contracts) {
        this.contracts = contracts;
    }

    public double getCmTdyClose() {
        return cmTdyClose;
    }

    public void setCmTdyClose(double cmTdyClose) {
        this.cmTdyClose = cmTdyClose;
    }

    public double getFoTdyClose() {
        return foTdyClose;
    }

    public void setFoTdyClose(double foTdyClose) {
        this.foTdyClose = foTdyClose;
    }

    public long getTdyOI() {
        return tdyOI;
    }

    public void setTdyOI(long tdyOI) {
        this.tdyOI = tdyOI;
    }

    public long getTdyDelivery() {
        return tdyDelivery;
    }

    public void setTdyDelivery(long tdyDelivery) {
        this.tdyDelivery = tdyDelivery;
    }

    public Date getTdyDate() {
        return tdyDate;
    }

    public void setTdyDate(Date tdyDate) {
        this.tdyDate = tdyDate;
    }

    public double getCmPrevsClose() {
        return cmPrevsClose;
    }

    public void setCmPrevsClose(double cmPrevsClose) {
        this.cmPrevsClose = cmPrevsClose;
    }

    public double getFoPrevsClose() {
        return foPrevsClose;
    }

    public void setFoPrevsClose(double foPrevsClose) {
        this.foPrevsClose = foPrevsClose;
    }

    public long getPrevsOI() {
        return prevsOI;
    }

    public void setPrevsOI(long prevsOI) {
        this.prevsOI = prevsOI;
    }

    public long getPrevsDelivery() {
        return prevsDelivery;
    }

    public void setPrevsDelivery(long prevsDelivery) {
        this.prevsDelivery = prevsDelivery;
    }

    public Date getPrevsDate() {
        return prevsDate;
    }

    public void setPrevsDate(Date prevsDate) {
        this.prevsDate = prevsDate;
    }

    public double getCmPrcntChgInClose() {
        return cmPrcntChgInClose;
    }

    public void setCmPrcntChgInClose(double cmPrcntChgInClose) {
        this.cmPrcntChgInClose = cmPrcntChgInClose;
    }

    public double getFoPrcntChgInClose() {
        return foPrcntChgInClose;
    }

    public void setFoPrcntChgInClose(double foPrcntChgInClose) {
        this.foPrcntChgInClose = foPrcntChgInClose;
    }

    public double getPrcntChgInOI() {
        return prcntChgInOI;
    }

    public void setPrcntChgInOI(double prcntChgInOI) {
        this.prcntChgInOI = prcntChgInOI;
    }

    public double getPrcntChgInDelivery() {
        return prcntChgInDelivery;
    }

    public void setPrcntChgInDelivery(double prcntChgInDelivery) {
        this.prcntChgInDelivery = prcntChgInDelivery;
    }

    public LocalDate getExpiryLocalDate() {
        return expiryLocalDate;
    }

    public void setExpiryLocalDate(LocalDate expiryLocalDate) {
        this.expiryLocalDate = expiryLocalDate;
    }

    public LocalDate getTdyLocalDate() {
        return tdyLocalDate;
    }

    public void setTdyLocalDate(LocalDate tdyLocalDate) {
        this.tdyLocalDate = tdyLocalDate;
    }

    public LocalDate getPrevsLocalDate() {
        return prevsLocalDate;
    }

    public void setPrevsLocalDate(LocalDate prevsLocalDate) {
        this.prevsLocalDate = prevsLocalDate;
    }

    public String getHammer() {
        return hammer;
    }

    public void setHammer(String hammer) {
        this.hammer = hammer;
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

    public long getCmTdyTraded() {
        return cmTdyTraded;
    }

    public void setCmTdyTraded(long cmTdyTraded) {
        this.cmTdyTraded = cmTdyTraded;
    }

}
