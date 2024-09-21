package org.samtuap.inong.domain.seller.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String toEmail);

    default Seller findByIdOrThrow(Long sellerId) {
        return findById(sellerId).orElseThrow(() -> new BaseCustomException(ID_NOT_FOUND));
    }
}
