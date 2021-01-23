package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseCashMarketTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NseCmRepo extends JpaRepository<NseCashMarketTab, Long> {
//    @Query("SELECT count(*) FROM nse_cash_market_tab t WHERE t.trade_date = ?1")
//    Integer dataCount(LocalDate tradeDate);
}
