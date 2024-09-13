package org.samtuap.inong.domain.farmNotice.repository;

import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmNoticeRepository extends JpaRepository<FarmNotice, Long> {
}
