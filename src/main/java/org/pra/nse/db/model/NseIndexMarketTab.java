package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "Nse_Index_Market_Tab")
public class NseIndexMarketTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @SequenceGenerator(name = "nse_index_market_seq_id", sequenceName = "nse_index_market_seq_id", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nse_index_market_seq_id")
    private Long id;

    private String symbol;
    private String idxName;
    private LocalDate tradeDate;
    private double open;
    private double high;
    private double low;
    private double close;
    private double PointsChgAbs;
    private double PointsChgPct;
    private long volume;
    private double turnOverInCrore;
    private double pe;
    private double pb;
    private double divYield;
    private String tds;
    private long tdn;


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
        idxName = null;
        tradeDate = null;
        open = 0;
        high = 0;
        low = 0;
        close = 0;
        PointsChgAbs = 0;
        PointsChgPct = 0;
        volume = 0;
        turnOverInCrore = 0;
        pe = 0;
        pb = 0;
        divYield = 0;
        tds = null;
        tdn = 0;
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

    public String getIdxName() {
        return idxName;
    }

    public void setIdxName(String idxName) {
        this.idxName = idxName;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
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

    public double getPointsChgAbs() {
        return PointsChgAbs;
    }

    public void setPointsChgAbs(double pointsChgAbs) {
        PointsChgAbs = pointsChgAbs;
    }

    public double getPointsChgPct() {
        return PointsChgPct;
    }

    public void setPointsChgPct(double pointsChgPct) {
        PointsChgPct = pointsChgPct;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getTurnOverInCrore() {
        return turnOverInCrore;
    }

    public void setTurnOverInCrore(double turnOverInCrore) {
        this.turnOverInCrore = turnOverInCrore;
    }

    public double getPe() {
        return pe;
    }

    public void setPe(double pe) {
        this.pe = pe;
    }

    public double getPb() {
        return pb;
    }

    public void setPb(double pb) {
        this.pb = pb;
    }

    public double getDivYield() {
        return divYield;
    }

    public void setDivYield(double divYield) {
        this.divYield = divYield;
    }

    public String getTds() {
        return tds;
    }

    public void setTds(String tds) {
        this.tds = tds;
    }

    public long getTdn() {
        return tdn;
    }

    public void setTdn(long tdn) {
        this.tdn = tdn;
    }

}
