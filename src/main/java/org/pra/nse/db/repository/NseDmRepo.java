package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseDeliveryMarketTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NseDmRepo extends JpaRepository<NseDeliveryMarketTab, Long> {
}
