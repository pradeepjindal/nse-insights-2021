package org.pra.nse.csv.bean.in;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;

public class FmBean {
    private String instrument;
    private String symbol;
    @JsonFormat(pattern="dd-MMM-yyyy")
    //@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)
    private Date expiry_Dt;
    private double strike_Pr;
    private String option_Typ;
    private double open;
    private double high;
    private double low;
    private double close;
    private double settle_Pr;
    private long contracts;
    private double val_InLakh;
    private long open_Int;
    private long chg_In_Oi;
    @JsonFormat(pattern="dd-MMM-yyyy")
    private Date timestamp;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FmBean fmBean = (FmBean) o;
        return Double.compare(fmBean.strike_Pr, strike_Pr) == 0 &&
                instrument.equals(fmBean.instrument) &&
                symbol.equals(fmBean.symbol) &&
                expiry_Dt.equals(fmBean.expiry_Dt) &&
                option_Typ.equals(fmBean.option_Typ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, symbol, expiry_Dt, strike_Pr, option_Typ);
    }

    @Override
    public String toString() {
        return "FoBean{" +
                "instrument='" + instrument + '\'' +
                ", symbol='" + symbol + '\'' +
                ", expiry_Dt=" + expiry_Dt +
                ", strike_Pr=" + strike_Pr +
                ", option_Typ='" + option_Typ + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", settle_Pr=" + settle_Pr +
                ", contracts=" + contracts +
                ", val_InLakh=" + val_InLakh +
                ", open_Int=" + open_Int +
                ", chg_In_Oi=" + chg_In_Oi +
                ", timestamp=" + timestamp +
                '}';
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

    public Date getExpiry_Dt() {
        return expiry_Dt;
    }

    public void setExpiry_Dt(Date expiry_Dt) {
        this.expiry_Dt = expiry_Dt;
    }

    public double getStrike_Pr() {
        return strike_Pr;
    }

    public void setStrike_Pr(double strike_Pr) {
        this.strike_Pr = strike_Pr;
    }

    public String getOption_Typ() {
        return option_Typ;
    }

    public void setOption_Typ(String option_Typ) {
        this.option_Typ = option_Typ;
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

    public double getSettle_Pr() {
        return settle_Pr;
    }

    public void setSettle_Pr(double settle_Pr) {
        this.settle_Pr = settle_Pr;
    }

    public long getContracts() {
        return contracts;
    }

    public void setContracts(long contracts) {
        this.contracts = contracts;
    }

    public double getVal_InLakh() {
        return val_InLakh;
    }

    public void setVal_InLakh(double val_InLakh) {
        this.val_InLakh = val_InLakh;
    }

    public long getOpen_Int() {
        return open_Int;
    }

    public void setOpen_Int(long open_Int) {
        this.open_Int = open_Int;
    }

    public long getChg_In_Oi() {
        return chg_In_Oi;
    }

    public void setChg_In_Oi(long chg_In_Oi) {
        this.chg_In_Oi = chg_In_Oi;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
