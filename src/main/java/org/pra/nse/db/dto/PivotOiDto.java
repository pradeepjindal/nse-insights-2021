package org.pra.nse.db.dto;

import java.time.LocalDate;

public class PivotOiDto {
    private String symbol;
    private LocalDate tradeDate;
    private int trade_dateRank;
    private long oiOne;
    private long oiTwo;
    private long oiThree;

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

    public int getTrade_dateRank() {
        return trade_dateRank;
    }

    public void setTrade_dateRank(int trade_dateRank) {
        this.trade_dateRank = trade_dateRank;
    }

    public long getOiOne() {
        return oiOne;
    }

    public void setOiOne(long oiOne) {
        this.oiOne = oiOne;
    }

    public long getOiTwo() {
        return oiTwo;
    }

    public void setOiTwo(long oiTwo) {
        this.oiTwo = oiTwo;
    }

    public long getOiThree() {
        return oiThree;
    }

    public void setOiThree(long oiThree) {
        this.oiThree = oiThree;
    }
}
