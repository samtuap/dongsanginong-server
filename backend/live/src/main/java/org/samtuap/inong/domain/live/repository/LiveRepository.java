package org.samtuap.inong.domain.live.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.live.entity.Live;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.LiveExceptionType.LIVE_NOT_FOUND;
import static org.samtuap.inong.common.exceptionType.LiveExceptionType.SESSION_NOT_FOUND;

public interface LiveRepository extends JpaRepository<Live, Long> {

    @Query("SELECT live FROM Live live WHERE live.endAt IS NULL")
    List<Live> findActiveLives();

    List<Live> findByFarmIdInAndEndAtIsNull(List<Long> farmIdList);

    Optional<Live> findBySessionId(String sessionId);

    default Live findBySessionIdOrThrow(String sessionId) {
        return findBySessionId(sessionId).orElseThrow(() -> new BaseCustomException(SESSION_NOT_FOUND));
    }

    default Live findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BaseCustomException(LIVE_NOT_FOUND));
    }
}
