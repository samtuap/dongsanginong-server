package org.samtuap.inong.domain.discount.repository;

import org.samtuap.inong.domain.discount.entity.Discount;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findAllByStartAt(LocalDate now);

    List<Discount> findAllByEndAtBefore(LocalDate now);

    List<Discount> findAll();

    boolean existsByPackageProduct(PackageProduct packageProduct);


}
