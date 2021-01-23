package org.pra.nse.csv.data;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AvgBeanNew implements CalcBeanNew {

    private String symbol;
    //@JsonFormat(pattern="yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate tradeDate;
    private Integer forDays;

    private BigDecimal atpSma;
    private BigDecimal volSma;
    private BigDecimal delSma;
    private BigDecimal foiSma;

    public String toCsvString() {
        return symbol +
                "," + tradeDate +
                "," + forDays +

                "," + atpSma +
                "," + volSma +
                "," + delSma +
                "," + foiSma;
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

    public BigDecimal getAtpSma() {
        return atpSma;
    }

    public void setAtpSma(BigDecimal atpSma) {
        this.atpSma = atpSma;
    }

    public BigDecimal getVolSma() {
        return volSma;
    }

    public void setVolSma(BigDecimal volSma) {
        this.volSma = volSma;
    }

    public BigDecimal getDelSma() {
        return delSma;
    }

    public void setDelSma(BigDecimal delSma) {
        this.delSma = delSma;
    }

    public BigDecimal getFoiSma() {
        return foiSma;
    }

    public void setFoiSma(BigDecimal foiSma) {
        this.foiSma = foiSma;
    }
}
