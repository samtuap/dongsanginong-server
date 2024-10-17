package org.samtuap.inong.domain.order.repository;


import org.samtuap.inong.domain.order.dto.PackageOrderCount;
import org.samtuap.inong.domain.order.dto.PackageProductOrderResponse;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.dto.TopPackageResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Ordering, Long> {

    @Query(value = "SELECT o.package_id FROM ordering o " +
            "GROUP BY o.package_id " +
            "ORDER BY COUNT(o.package_id) DESC LIMIT 10", nativeQuery = true)
    List<Long> findTop10PackageIdWithMostOrders();

    Long countByPackageId(Long packageId);

    List<Ordering> findByFarmId(Long farm);

    List<Ordering> findByMemberId(Long memberId);

    Optional<Ordering> findByPackageIdAndMemberId(Long packageId, Long memberId);

    Page<Ordering> findAllByMemberId(Long memberId, Pageable pageable);

    List<Ordering> findAllByMemberId(Long memberId);

    @Query("SELECT new org.samtuap.inong.domain.order.dto.PackageOrderCount(o.packageId, COUNT(o.id)) FROM Ordering o WHERE o.farmId = :farmId AND o.createdAt >= :startAt AND o.createdAt <= :endAt GROUP BY o.packageId")
    List<PackageOrderCount> findAllByFarmIdAndBetweenStartAtAndEndAt(Long farmId, LocalDateTime startAt, LocalDateTime endAt);

}
