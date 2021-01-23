package org.pra.nse.db.repository;

import org.pra.nse.db.model.CalcAvgTabNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalcAvgRepoNew extends JpaRepository<CalcAvgTabNew, Long> {
    Integer countByTradeDate(LocalDate tradeDate);
    List<CalcAvgTabNew> findByTradeDateAndForDays(LocalDate tradeDate, Integer forDays);
    List<CalcAvgTabNew> findByForDaysAndTradeDateIsBetween(Integer forDays, LocalDate tradeDateFrom, LocalDate tradeDateTo);
    List<CalcAvgTabNew> findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(Integer forDays, LocalDate fromDate, LocalDate toDate);

}
