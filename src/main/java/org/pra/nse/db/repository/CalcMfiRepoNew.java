package org.pra.nse.db.repository;

import org.pra.nse.db.model.CalcMfiTabNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalcMfiRepoNew extends JpaRepository<CalcMfiTabNew, Long> {
    Integer countByTradeDate(LocalDate tradeDate);
    List<CalcMfiTabNew> findByTradeDateAndForDays(LocalDate tradeDate, Integer forDays);
    List<CalcMfiTabNew> findByForDaysAndTradeDateIsBetween(Integer forDays, LocalDate tradeDateFrom, LocalDate tradeDateTo);
    List<CalcMfiTabNew> findByForDaysAndTradeDateIsBetweenOrderBySymbolAscTradeDateAsc(Integer forDays, LocalDate fromDate, LocalDate toDate);
}
