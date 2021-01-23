package org.pra.nse.csv.bean.in;

import java.util.Date;
import java.util.Objects;

public class DmBean {
    private long recType;
    private long srNo;
    private String symbol;
    private String securityType;
    private long tradedQty;
    private long deliverableQty;
    private double deliveryToTradeRatio;
    private Date tradeDate;

    @Override
    public String toString() {
        return "DmBean{" +
                "recType=" + recType +
                ", srNo=" + srNo +
                ", symbol='" + symbol + '\'' +
                ", securityType='" + securityType + '\'' +
                ", tradedQty=" + tradedQty +
                ", deliverableQty=" + deliverableQty +
                ", deliveryToTradeRatio=" + deliveryToTradeRatio +
                ", tradeDate=" + tradeDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DmBean dmBean = (DmBean) o;
        return symbol.equals(dmBean.symbol) &&
                securityType.equals(dmBean.securityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, securityType);
    }

    public long getRecType() {
        return recType;
    }

    public void setRecType(long recType) {
        this.recType = recType;
    }

    public long getSrNo() {
        return srNo;
    }

    public void setSrNo(long srNo) {
        this.srNo = srNo;
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

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }
}
