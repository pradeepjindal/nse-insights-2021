package org.pra.nse.db.repository;

import org.pra.nse.db.model.CalcMfiTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalcMfiRepo extends JpaRepository<CalcMfiTab, Long> {
    Integer countByTradeDate(LocalDate tradeDate);
    List<CalcMfiTab> findByTradeDateAndForDays(LocalDate tradeDate, Integer forDays);
    List<CalcMfiTab> findByForDaysAndTradeDateIsBetween(Integer forDays, LocalDate tradeDateFrom, LocalDate tradeDateTo);
    List<CalcMfiTab> findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(Integer forDays, LocalDate fromDate, LocalDate toDate);

    void deleteByTradeDate(LocalDate forDate);
}
