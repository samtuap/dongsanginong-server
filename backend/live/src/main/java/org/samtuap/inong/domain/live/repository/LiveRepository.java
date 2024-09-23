package org.samtuap.inong.domain.live.repository;

import org.samtuap.inong.domain.live.entity.Live;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LiveRepository extends JpaRepository<Live, Long> {

    @Query("SELECT live FROM Live live WHERE live.endAt IS NULL")
    List<Live> findActiveLives();
}
