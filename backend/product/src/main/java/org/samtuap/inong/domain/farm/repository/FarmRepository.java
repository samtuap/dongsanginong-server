package org.samtuap.inong.domain.farm.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.FARM_NOT_FOUND;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    default Farm findByIdOrThrow(Long farmId) {
        return findById(farmId).orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));
    }

    default Farm findBySellerIdOrThrow(Long sellerId) {
        return findById(sellerId).orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));
    }

    Page<Farm> findAll(Specification<Farm> specification, Pageable pageable);

    // Feign 요청용
    List<Farm> findByIdIn(List<Long> farmFavoriteIds);
}
