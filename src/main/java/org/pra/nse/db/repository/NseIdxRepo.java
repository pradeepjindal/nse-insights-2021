package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseIndexMarketTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NseIdxRepo extends JpaRepository<NseIndexMarketTab, Long> {
}
