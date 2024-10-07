package org.samtuap.inong.domain.product.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

public interface PackageProductImageRepository extends JpaRepository<PackageProductImage, Long> {
    List<PackageProductImage> findAllByPackageProduct(PackageProduct packageProduct);

    void deleteByPackageProductAndImageUrl(PackageProduct packageProduct, String imageUrl);

    PackageProductImage findFirstByPackageProduct(PackageProduct packageProduct);



}