package org.samtuap.inong.domain.farmNotice.repository;

import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmNoticeRepository extends JpaRepository<FarmNotice, Long> {

    List<FarmNotice> findByFarm(Farm farm);
}
