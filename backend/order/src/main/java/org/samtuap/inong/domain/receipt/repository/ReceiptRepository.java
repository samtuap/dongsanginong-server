package org.samtuap.inong.domain.receipt.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.OrderExceptionType;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByOrder(Ordering order);

    default Receipt findByOrderOrThrow(Ordering order) {
        return findByOrder(order).orElseThrow(() -> new BaseCustomException(ORDER_NOT_FOUND));
    }

    @Query("SELECT new org.samtuap.inong.domain.order.dto.SalesDataGetResponse(COUNT(*), SUM(r.totalPrice)) " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND r.createdAt >= :startTime AND r.createdAt <= :endTime")
    SalesDataGetResponse findSalesData(@Param("farmId") Long farmId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT new org.samtuap.inong.domain.order.dto.SalesDataGetResponse(COUNT(*), SUM(r.totalPrice)) " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND r.createdAt >= :startTime AND r.createdAt <= :endTime AND r.order.isFirst = true")
    SalesDataGetResponse findSalesDataFirstOnly(@Param("farmId") Long farmId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND r.createdAt >= :startTime AND r.createdAt <= :endTime")
    List<Receipt> findAllByOrderFarmId(@Param("farmId") Long farmId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND r.createdAt >= :startTime AND r.createdAt <= :endTime AND r.order.isFirst = true")
    List<Receipt> findAllByOrderFarmIdFirstOnly(@Param("farmId") Long farmId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
