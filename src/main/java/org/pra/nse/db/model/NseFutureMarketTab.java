package org.pra.nse.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "Nse_Future_Market_Tab")
public class NseFutureMarketTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @SequenceGenerator(name = "nse_future_market_seq_id", sequenceName = "nse_future_market_seq_id", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nse_future_market_seq_id")
    private Long id;
    private String instrument;
    private String symbol;
    private LocalDate expiryDate;
    private double strikePrice;
    private String optionType;
    private double open;
    private double high;
    private double low;
    private double close;
    private double settlePrice;
    private long contracts;
    private double valueInLakh;
    private long openInt;
    private long changeInOi;
    private LocalDate tradeDate;


    public void reset() {
        id = null;
        instrument = null;
        symbol = null;
        expiryDate = null;
        strikePrice = 0;
        optionType = null;
        open = 0;
        high = 0;
        low = 0;
        close = 0;
        settlePrice = 0;
        contracts = 0;
        valueInLakh = 0;
        openInt = 0;
        changeInOi = 0;
        tradeDate = null;
    }


    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
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

    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    
    public void setExpiryDate(LocalDate expiry_date) {
        this.expiryDate = expiry_date;
    }

    
    public double getStrikePrice() {
        return strikePrice;
    }

    
    public void setStrikePrice(double strike_Price) {
        this.strikePrice = strike_Price;
    }

    
    public String getOptionType() {
        return optionType;
    }

    
    public void setOptionType(String option_Type) {
        this.optionType = option_Type;
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

    
    public double getSettlePrice() {
        return settlePrice;
    }

    
    public void setSettlePrice(double settle_Price) {
        this.settlePrice = settle_Price;
    }

    
    public long getContracts() {
        return contracts;
    }

    
    public void setContracts(long contracts) {
        this.contracts = contracts;
    }

    
    public double getValueInLakh() {
        return valueInLakh;
    }

    
    public void setValueInLakh(double value_in_lakh) {
        this.valueInLakh = value_in_lakh;
    }

    
    public long getOpenInt() {
        return openInt;
    }

    
    public void setOpenInt(long open_int) {
        this.openInt = open_int;
    }

    
    public long getChangeInOi() {
        return changeInOi;
    }

    
    public void setChangeInOi(long change_in_oi) {
        this.changeInOi = change_in_oi;
    }

    
    public LocalDate getTradeDate() {
        return tradeDate;
    }

    
    public void setTradeDate(LocalDate trade_date) {
        this.tradeDate = trade_date;
    }
}
