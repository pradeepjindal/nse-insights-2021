package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "Nse_Lot_Size_Tab")
public class NseLotSizeTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @SequenceGenerator(name = "nse_lot_size_seq_id", sequenceName = "nse_lot_size_seq_id", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nse_lot_size_seq_id")
    private Long id;

    private String symbol;
    private LocalDate tradeDate;
    private Integer tdn;
    private LocalDate expiryDate;
    private Integer edn;
    private Integer lotSize;

    private LocalDate fileDate;
    private Integer fdn;


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
        tdn = 0;
        expiryDate = null;
        edn = 0;
        lotSize = 0;

        fileDate = null;
        fdn = 0;
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

    public Integer getTdn() {
        return tdn;
    }

    public void setTdn(Integer tdn) {
        this.tdn = tdn;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getEdn() {
        return edn;
    }

    public void setEdn(Integer edn) {
        this.edn = edn;
    }

    public Integer getLotSize() {
        return lotSize;
    }

    public void setLotSize(Integer lotSize) {
        this.lotSize = lotSize;
    }

    public LocalDate getFileDate() {
        return fileDate;
    }

    public void setFileDate(LocalDate fileDate) {
        this.fileDate = fileDate;
    }

    public Integer getFdn() {
        return fdn;
    }

    public void setFdn(Integer fdn) {
        this.fdn = fdn;
    }

}
