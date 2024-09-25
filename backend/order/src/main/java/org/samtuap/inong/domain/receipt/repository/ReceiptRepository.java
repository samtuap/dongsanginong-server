package org.samtuap.inong.domain.receipt.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.OrderExceptionType;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByOrder(Ordering order);

    default Receipt findByOrderOrThrow(Ordering order) {
        return findByOrder(order).orElseThrow(() -> new BaseCustomException(ORDER_NOT_FOUND));
    }
}
