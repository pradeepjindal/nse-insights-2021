package org.pra.nse.db.model;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "Nse_Delivery_Market_Tab")
public class NseDeliveryMarketTab implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "auto_gen")
//    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @SequenceGenerator(name = "nse_delivery_market_seq_id", sequenceName = "nse_delivery_market_seq_id", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nse_delivery_market_seq_id")
    private Long id;

    private String symbol;
    private String securityType;
    private long tradedQty;
    private long deliverableQty;
    private double deliveryToTradeRatio;
    private LocalDate tradeDate;


    public void reset() {
        id = null;
        symbol = null;
        securityType = null;
        tradedQty = 0;
        deliverableQty = 0;
        deliveryToTradeRatio = 0;
        tradeDate = null;
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

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public long getTradedQty() {
        return tradedQty;
    }

    public void setTradedQty(long tradedQty) {
        this.tradedQty = tradedQty;
    }

    public long getDeliverableQty() {
        return deliverableQty;
    }

    public void setDeliverableQty(long deliverableQty) {
        this.deliverableQty = deliverableQty;
    }

    public double getDeliveryToTradeRatio() {
        return deliveryToTradeRatio;
    }

    public void setDeliveryToTradeRatio(double deliveryToTradeRatio) {
        this.deliveryToTradeRatio = deliveryToTradeRatio;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }
}
