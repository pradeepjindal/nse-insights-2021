package org.pra.nse.db.repository;

import org.pra.nse.db.model.NseIndexTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NseNxRepo extends JpaRepository<NseIndexTab, Long> {
}
