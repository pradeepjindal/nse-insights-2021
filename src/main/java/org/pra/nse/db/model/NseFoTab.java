package org.pra.nse.db.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Nse_Fo_Tab")
public class NseFoTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @SequenceGenerator(name = "nse_fo_seq_id", sequenceName = "nse_fo_seq_id", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nse_fo_seq_id")
    private Long id;

    private String symbol;
    private LocalDate tradeDate;
    private LocalDate expiryDate;
    private String instrument;
    private Integer quantity;
    private Integer contracts;
    private Integer lotSize;
    private BigDecimal turnover;
    private LocalDate fileDate;

    private Integer tdn;
    private Integer edn;
    private Integer fdn;

    private String nse;


//    @Id
//    @GeneratedValue(generator = "question_generator")
//    @SequenceGenerator(
//            name = "question_generator",
//            sequenceName = "question_sequence",
//            initialValue = 1000
//    )


    public void reset() {
        id = null;
        symbol = null;
        tradeDate = null;
        expiryDate = null;
        instrument = null;
        quantity = 0;
        contracts = 0;
        lotSize = 0;
        turnover = null;
        fileDate = null;
        tdn = 0;
        edn = 0;
        fdn = 0;
        nse = null;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getContracts() {
        return contracts;
    }

    public void setContracts(Integer contracts) {
        this.contracts = contracts;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public LocalDate getFileDate() {
        return fileDate;
    }

    public void setFileDate(LocalDate fileDate) {
        this.fileDate = fileDate;
    }

    public Integer getTdn() {
        return tdn;
    }

    public void setTdn(Integer tdn) {
        this.tdn = tdn;
    }

    public Integer getEdn() {
        return edn;
    }

    public void setEdn(Integer edn) {
        this.edn = edn;
    }

    public Integer getFdn() {
        return fdn;
    }

    public void setFdn(Integer fdn) {
        this.fdn = fdn;
    }

    public String getNse() {
        return nse;
    }

    public void setNse(String nse) {
        this.nse = nse;
    }

}
