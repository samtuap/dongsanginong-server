package org.samtuap.inong.domain.farm.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.FARM_NOT_FOUND;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    default Farm findByIdOrThrow(Long farmId) {
        return findById(farmId).orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));
    }
}
