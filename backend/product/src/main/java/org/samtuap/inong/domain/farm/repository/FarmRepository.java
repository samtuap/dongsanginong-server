package org.samtuap.inong.domain.farm.repository;

import org.samtuap.inong.domain.farm.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Long> {
}
