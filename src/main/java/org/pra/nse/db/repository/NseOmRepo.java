package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseOptionMarketTab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NseOmRepo extends CrudRepository<NseOptionMarketTab, Long> {
//    @Query("SELECT count(*) FROM nse_future_market_tab t WHERE t.trade_date = ?1")
//    Integer dataCount(LocalDate tradeDate);
}
