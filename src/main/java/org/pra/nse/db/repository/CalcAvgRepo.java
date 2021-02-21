package org.pra.nse.db.repository;

import org.pra.nse.db.model.CalcAvgTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalcAvgRepo extends JpaRepository<CalcAvgTab, Long> {
    Integer countByTradeDate(LocalDate tradeDate);
    List<CalcAvgTab> findByTradeDateAndForDays(LocalDate tradeDate, Integer forDays);
    List<CalcAvgTab> findByForDaysAndTradeDateIsBetween(Integer forDays, LocalDate tradeDateFrom, LocalDate tradeDateTo);
    List<CalcAvgTab> findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(Integer forDays, LocalDate fromDate, LocalDate toDate);

    void deleteByTradeDate(LocalDate forDate);
}
