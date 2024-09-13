package org.samtuap.inong.domain.live.repository;

import org.samtuap.inong.domain.live.entity.Live;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveRepository extends JpaRepository<Live, Long> {
}
