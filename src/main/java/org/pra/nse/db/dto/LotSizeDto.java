package org.pra.nse.db.dto;

import java.time.LocalDate;

public class LotSizeDto {

    private String symbol;
    private String tradeMonth;
    private LocalDate expiryDate;
    private Long lotSize;


    @Override
    public String toString() {
        return "LotSizeDto{" +
                "symbol='" + symbol + '\'' +
                ", tradeMonth='" + tradeMonth + '\'' +
                ", expiryDate=" + expiryDate +
                ", lotSize=" + lotSize +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTradeMonth() {
        return tradeMonth;
    }

    public void setTradeMonth(String tradeMonth) {
        this.tradeMonth = tradeMonth;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getLotSize() {
        return lotSize;
    }

    public void setLotSize(Long lotSize) {
        this.lotSize = lotSize;
    }

}
