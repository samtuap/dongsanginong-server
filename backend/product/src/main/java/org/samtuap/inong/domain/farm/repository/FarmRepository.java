package org.samtuap.inong.domain.farm.repository;

import feign.Param;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.FARM_NOT_FOUND;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    default Farm findByIdOrThrow(Long farmId) {
        return findById(farmId).orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));
    }

    Optional<Farm> findBySellerId(Long sellerId);

    default Farm findBySellerIdOrThrow(Long sellerId) {
        return findBySellerId(sellerId).orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));
    }

    Page<Farm> findAll(Specification<Farm> specification, Pageable pageable);

    // Feign 요청용
    List<Farm> findByIdIn(List<Long> farmFavoriteIds);

    @Query("SELECT f.farmName FROM Farm f WHERE f.id = :farmId")
    String getFarmNameById(@Param("farmId") Long farmId);


    boolean existsBySellerId(Long sellerId);
}
