package org.samtuap.inong.domain.product.repository;

import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageProductRepository extends JpaRepository<PackageProduct, Long> {
    @Query("SELECT p FROM PackageProduct p WHERE p.id IN :ids")
    List<PackageProduct> findAllByIds(@Param("ids") List<Long> ids);
}
