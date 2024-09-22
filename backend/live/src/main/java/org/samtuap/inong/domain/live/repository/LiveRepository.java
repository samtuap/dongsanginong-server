package org.samtuap.inong.domain.live.repository;

import org.samtuap.inong.domain.live.entity.Live;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveRepository extends JpaRepository<Live, Long> {

    List<Live> findByFarmIdInAndEndAtIsNull(List<Long> farmIdList);
}
