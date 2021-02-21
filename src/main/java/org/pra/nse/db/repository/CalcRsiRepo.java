package org.pra.nse.db.repository;

import org.pra.nse.db.model.CalcRsiTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalcRsiRepo extends JpaRepository<CalcRsiTab, Long> {
    Integer countByTradeDate(LocalDate tradeDate);
    List<CalcRsiTab> findByTradeDateAndForDays(LocalDate tradeDate, Integer forDays);
    List<CalcRsiTab> findByForDaysAndTradeDateIsBetween(Integer forDays, LocalDate tradeDateFrom, LocalDate tradeDateTo);
    List<CalcRsiTab> findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(Integer forDays, LocalDate fromDate, LocalDate toDate);

    void deleteByTradeDate(LocalDate forDate);
}
