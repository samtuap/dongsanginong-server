package org.samtuap.inong.domain.farm.repository;

import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.springframework.data.jpa.repository.JpaRepository;



public interface FarmCategoryRelationRepository extends JpaRepository<FarmCategoryRelation, Long> {
    FarmCategoryRelation findByFarmId(Long farmId);
}
