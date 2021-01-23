package org.pra.nse.csv.data;

import java.time.LocalDate;

public interface CalcBeanNew {
    public String getSymbol();
    public void setSymbol(String symbol);

    public LocalDate getTradeDate();
    public void setTradeDate(LocalDate tradeDate);

    public Integer getForDays();
    public void setForDays(Integer forDays);

    public String toCsvString();
}
