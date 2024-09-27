package org.samtuap.inong.domain.product.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.PRODUCT_NOT_FOUND;

public interface PackageProductRepository extends JpaRepository<PackageProduct, Long> {
    @Query("SELECT p FROM PackageProduct p WHERE p.id IN :ids")
    List<PackageProduct> findAllByIds(@Param("ids") List<Long> ids);

    @Query("SELECT p FROM PackageProduct p WHERE p.farm.sellerId = :sellerId")
    Page<PackageProduct> findBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    default PackageProduct findByIdOrThrow(Long packageId){
        return findById(packageId).orElseThrow(()->new BaseCustomException(PRODUCT_NOT_FOUND));
    }

    boolean existsByFarmIdAndDeletedAtIsNull(Long farmId);

    @Query(value = "SELECT p.* FROM package_product AS p WHERE p.id IN :ids", nativeQuery = true)
    List<PackageProduct> findAllByIdContainDeleted(List<Long> ids);
}