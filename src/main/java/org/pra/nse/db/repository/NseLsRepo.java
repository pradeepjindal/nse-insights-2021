package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseLotSizeTab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NseLsRepo extends CrudRepository<NseLotSizeTab, Long> {
//    @Query("SELECT count(*) FROM nse_future_market_tab t WHERE t.trade_date = ?1")
//    Integer dataCount(LocalDate tradeDate);
}
