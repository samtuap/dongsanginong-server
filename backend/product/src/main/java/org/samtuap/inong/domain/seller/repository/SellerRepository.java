package org.samtuap.inong.domain.seller.repository;

import org.samtuap.inong.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
