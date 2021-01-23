package org.pra.nse.csv.data;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RsiBeanNew implements CalcBeanNew {

    private String symbol;
    //@JsonFormat(pattern="yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate tradeDate;
    private Integer forDays;

    private BigDecimal openRsiSma;
    private BigDecimal highRsiSma;
    private BigDecimal lowRsiSma;
    private BigDecimal closeRsiSma;
    private BigDecimal lastRsiSma;
    private BigDecimal atpRsiSma;
    private BigDecimal hlmRsiSma;
    private BigDecimal ohlcRsiSma;
    private BigDecimal delRsiSma;

    public String toCsvString() {
        return symbol +
                "," + tradeDate +
                "," + forDays +

                "," + openRsiSma +
                "," + highRsiSma +
                "," + lowRsiSma +
                "," + closeRsiSma +
                "," + lastRsiSma +
                "," + atpRsiSma +
                "," + hlmRsiSma +
                "," + ohlcRsiSma +
                "," + delRsiSma;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Integer getForDays() {
        return forDays;
    }

    public void setForDays(Integer forDays) {
        this.forDays = forDays;
    }

    public BigDecimal getOpenRsiSma() {
        return openRsiSma;
    }

    public void setOpenRsiSma(BigDecimal openRsiSma) {
        this.openRsiSma = openRsiSma;
    }

    public BigDecimal getHighRsiSma() {
        return highRsiSma;
    }

    public void setHighRsiSma(BigDecimal highRsiSma) {
        this.highRsiSma = highRsiSma;
    }

    public BigDecimal getLowRsiSma() {
        return lowRsiSma;
    }

    public void setLowRsiSma(BigDecimal lowRsiSma) {
        this.lowRsiSma = lowRsiSma;
    }

    public BigDecimal getCloseRsiSma() {
        return closeRsiSma;
    }

    public void setCloseRsiSma(BigDecimal closeRsiSma) {
        this.closeRsiSma = closeRsiSma;
    }

    public BigDecimal getLastRsiSma() {
        return lastRsiSma;
    }

    public void setLastRsiSma(BigDecimal lastRsiSma) {
        this.lastRsiSma = lastRsiSma;
    }

    public BigDecimal getAtpRsiSma() {
        return atpRsiSma;
    }

    public void setAtpRsiSma(BigDecimal atpRsiSma) {
        this.atpRsiSma = atpRsiSma;
    }

    public BigDecimal getHlmRsiSma() {
        return hlmRsiSma;
    }

    public void setHlmRsiSma(BigDecimal hlmRsiSma) {
        this.hlmRsiSma = hlmRsiSma;
    }

    public BigDecimal getOhlcRsiSma() {
        return ohlcRsiSma;
    }

    public void setOhlcRsiSma(BigDecimal ohlcRsiSma) {
        this.ohlcRsiSma = ohlcRsiSma;
    }

    public BigDecimal getDelRsiSma() {
        return delRsiSma;
    }

    public void setDelRsiSma(BigDecimal delRsiSma) {
        this.delRsiSma = delRsiSma;
    }

}
