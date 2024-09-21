package org.samtuap.inong.domain.product.repository;

import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageProductImageRepository extends JpaRepository<PackageProductImage, Long> {
    List<PackageProductImage> findAllByPackageProduct(PackageProduct packageProduct);
}