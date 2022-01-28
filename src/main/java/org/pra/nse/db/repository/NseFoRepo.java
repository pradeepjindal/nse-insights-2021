package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseFoTab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NseFoRepo extends CrudRepository<NseFoTab, Long> {
//    @Query("SELECT count(*) FROM nse_future_market_tab t WHERE t.trade_date = ?1")
//    Integer dataCount(LocalDate tradeDate);

    List<NseFoTab> findByTradeDate(LocalDate tradeDate);
}
