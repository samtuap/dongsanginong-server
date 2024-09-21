package org.samtuap.inong.domain.farm.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.FarmCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.FARM_NOT_FOUND;

public interface FarmCategoryRepository extends JpaRepository<FarmCategory, Long> {
    default FarmCategory findByIdOrThrow(Long categoryId) {
        return findById(categoryId).orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));
    }
}
