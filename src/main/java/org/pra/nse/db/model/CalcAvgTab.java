package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Calc_avg_Tab")
public class CalcAvgTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(name = "calc_avg_seq", sequenceName = "calc_avg_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_avg_seq")
    private Long id;
    private String symbol;
    private LocalDate tradeDate;

    private String tds;
    private Integer forDays;

    @Column(name = "atp_Sma")
    private BigDecimal atpSma;

    @Column(name = "trd_Sma")
    private BigDecimal trdSma;

    @Column(name = "del_Sma")
    private BigDecimal delSma;

    @Column(name = "foi_Sma")
    private BigDecimal foiSma;

    public void reset() {
        id = null;
        symbol = null;
        tradeDate = null;
        tds = null;
        forDays = null;

        atpSma = null;
        trdSma = null;
        delSma = null;
        foiSma = null;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }



    public String getTds() {
        return tds;
    }

    public void setTds(String tds) {
        this.tds = tds;
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

    public BigDecimal getTrdSma() {
        return trdSma;
    }

    public void setTrdSma(BigDecimal trdSma) {
        this.trdSma = trdSma;
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
