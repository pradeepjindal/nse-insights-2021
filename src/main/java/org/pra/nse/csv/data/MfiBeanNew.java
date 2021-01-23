package org.pra.nse.csv.data;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MfiBeanNew implements CalcBeanNew {

    private String symbol;
    //@JsonFormat(pattern="yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate tradeDate;
    private Integer forDays;

    private BigDecimal volMfi;
    private BigDecimal delMfi;

    public String toCsvString() {
        return symbol +
                "," + tradeDate +
                "," + forDays +

                "," + volMfi +
                "," + delMfi;
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

    public BigDecimal getVolMfi() {
        return volMfi;
    }

    public MfiBeanNew setVolMfi(BigDecimal volMfi) {
        this.volMfi = volMfi;
        return this;
    }

    public BigDecimal getDelMfi() {
        return delMfi;
    }

    public MfiBeanNew setDelMfi(BigDecimal delMfi) {
        this.delMfi = delMfi;
        return this;
    }
}
