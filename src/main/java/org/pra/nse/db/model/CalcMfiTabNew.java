package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Calc_Mfi_Tab_new")
public class CalcMfiTabNew implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(name = "calc_mfi_seq_new", sequenceName = "calc_mfi_seq_new", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_mfi_seq_new")
    private Long id;
    private String symbol;
    private LocalDate tradeDate;

    private String tds;
    private Integer forDays;

    @Column(name = "vol_atp_mfi_Sma")
    private BigDecimal volAtpMfiSma;

    @Column(name = "del_atp_mfi_Sma")
    private BigDecimal delAtpMfiSma;


    public void reset() {
        id = null;
        symbol = null;
        tradeDate = null;
        tds = null;
        forDays = null;

        volAtpMfiSma = null;
        delAtpMfiSma = null;
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

    public BigDecimal getVolAtpMfiSma() {
        return volAtpMfiSma;
    }

    public void setVolAtpMfiSma(BigDecimal volAtpMfiSma) {
        this.volAtpMfiSma = volAtpMfiSma;
    }

    public BigDecimal getDelAtpMfiSma() {
        return delAtpMfiSma;
    }

    public void setDelAtpMfiSma(BigDecimal delAtpMfiSma) {
        this.delAtpMfiSma = delAtpMfiSma;
    }

}
