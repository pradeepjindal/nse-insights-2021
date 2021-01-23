package org.pra.nse.db.dto;

import java.time.LocalDate;

public class CmTdrDto {
    private LocalDate tradeDate;
    private Long rank;

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

}
