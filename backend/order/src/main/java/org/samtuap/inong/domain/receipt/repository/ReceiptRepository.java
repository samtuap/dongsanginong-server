package org.samtuap.inong.domain.receipt.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.OrderExceptionType;
import org.samtuap.inong.common.exceptionType.ReceiptExceptionType;
import org.samtuap.inong.domain.order.dto.SalesDataByYearAndMonth;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;
import static org.samtuap.inong.common.exceptionType.ReceiptExceptionType.RECEIPT_NOT_FOUND;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByOrder(Ordering order);

    default Receipt findByOrderOrThrow(Ordering order) {
        return findByOrder(order).orElseThrow(() -> new BaseCustomException(ORDER_NOT_FOUND));
    }

    @Query("SELECT new org.samtuap.inong.domain.order.dto.SalesDataByYearAndMonth(COUNT(*), SUM(r.totalPrice)) " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month AND r.order.isFirst = :isFirst AND r.order.canceledAt IS NULL")
    SalesDataByYearAndMonth findSalesDataByYearAndMonthFirstOnly(@Param("farmId") Long farmId, @Param("year") int year, @Param("month") int month, @Param("isFirst") boolean isFirst);

    @Query("SELECT new org.samtuap.inong.domain.order.dto.SalesDataByYearAndMonth(COUNT(*), SUM(r.totalPrice)) " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month AND r.order.canceledAt IS NULL")
    SalesDataByYearAndMonth findSalesDataByYearAndMonth(@Param("farmId") Long farmId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT r " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND r.createdAt >= :startTime AND r.createdAt <= :endTime AND r.order.isFirst = true AND r.order.canceledAt IS NULL " +
            "ORDER BY r.createdAt DESC")
    Page<Receipt> findAllByOrderFarmIdFirstOnly(Pageable pageable, @Param("farmId") Long farmId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r " +
            "FROM Receipt r " +
            "WHERE r.order.farmId = :farmId AND r.createdAt >= :startTime AND r.createdAt <= :endTime AND r.order.canceledAt IS NULL " +
            "ORDER BY r.createdAt DESC")
    Page<Receipt> findAllByOrderFarmId(Pageable pageable, @Param("farmId") Long farmId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    default Receipt findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BaseCustomException(RECEIPT_NOT_FOUND));
    }


}
