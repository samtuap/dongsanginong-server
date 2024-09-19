package org.samtuap.inong.domain.seller.repository;

import org.samtuap.inong.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String toEmail);
}
