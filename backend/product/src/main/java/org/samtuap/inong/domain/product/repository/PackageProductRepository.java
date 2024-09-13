package org.samtuap.inong.domain.product.repository;

import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageProductRepository extends JpaRepository<PackageProduct, Long> {
}
