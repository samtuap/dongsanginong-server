package org.samtuap.inong.domain.farm.repository;

import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FarmCategoryRelationRepository extends JpaRepository<FarmCategoryRelation, Long> {
    List<FarmCategoryRelation> findAllByFarmId(Long farmId);

    void deleteAllByFarm(Farm farm);
}
