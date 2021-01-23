package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Calc_Rsi_Tab_new")
public class CalcRsiTabNew implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(name = "calc_rsi_seq_new", sequenceName = "calc_rsi_seq_new", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_rsi_seq_new")
    private Long id;
    private String symbol;
    private LocalDate tradeDate;

    private String tds;
    private Integer forDays;

    @Column(name = "open_rsi_Sma")
    private BigDecimal openRsiSma;

    @Column(name = "high_rsi_Sma")
    private BigDecimal highRsiSma;

    @Column(name = "low_rsi_Sma")
    private BigDecimal lowRsiSma;

    @Column(name = "close_rsi_Sma")
    private BigDecimal closeRsiSma;

    @Column(name = "last_rsi_Sma")
    private BigDecimal lastRsiSma;

    @Column(name = "atp_rsi_Sma")
    private BigDecimal atpRsiSma;

    @Column(name = "hlm_rsi_Sma")
    private BigDecimal hlmRsiSma;

    @Column(name = "ohlc_rsi_Sma")
    private BigDecimal ohlcRsiSma;

    @Column(name = "del_rsi_Sma")
    private BigDecimal delRsiSma;

    public void reset() {
        id = null;
        symbol = null;
        tradeDate = null;
        tds = null;
        forDays = null;

        openRsiSma = null;
        highRsiSma = null;
        lowRsiSma = null;
        closeRsiSma = null;
        lastRsiSma = null;
        atpRsiSma = null;
        hlmRsiSma = null;
        ohlcRsiSma = null;
        delRsiSma = null;
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
