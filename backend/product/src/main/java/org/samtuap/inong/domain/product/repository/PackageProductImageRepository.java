package org.samtuap.inong.domain.product.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

public interface PackageProductImageRepository extends JpaRepository<PackageProductImage, Long> {
    Optional<PackageProductImage> findByPackageProductId(Long packageProductId);

    default PackageProductImage findByPackageProductIdOrThrow(Long packageProductId){
        return findByPackageProductId(packageProductId).orElseThrow(()->new BaseCustomException(PRODUCT_IMAGE_NOT_FOUND));
    }
}
