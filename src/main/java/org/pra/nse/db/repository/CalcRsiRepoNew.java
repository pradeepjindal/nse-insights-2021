package org.pra.nse.db.repository;

import org.pra.nse.db.model.CalcRsiTabNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalcRsiRepoNew extends JpaRepository<CalcRsiTabNew, Long> {
    Integer countByTradeDate(LocalDate tradeDate);
    List<CalcRsiTabNew> findByTradeDateAndForDays(LocalDate tradeDate, Integer forDays);
    List<CalcRsiTabNew> findByForDaysAndTradeDateIsBetween(Integer forDays, LocalDate tradeDateFrom, LocalDate tradeDateTo);
    List<CalcRsiTabNew> findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(Integer forDays, LocalDate fromDate, LocalDate toDate);
}
