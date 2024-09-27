package org.samtuap.inong.domain.farmNotice.repository;

import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmNoticeRepository extends JpaRepository<FarmNotice, Long> {

    Page<FarmNotice> findByFarm(Farm farm, Pageable pageable);
    FarmNotice findByIdAndFarm(Long id, Farm farm);
}
