package org.samtuap.inong.domain.order.repository;


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

    @Query("SELECT o.packageId FROM Ordering o " +
            "GROUP BY o.packageId " +
            "ORDER BY COUNT(o.packageId) DESC")
    List<Long> findTop10PackageIdWithMostOrders();

    List<Ordering> findByFarmId(Long farm);

    List<Ordering> findByMemberId(Long memberId);

    Optional<Ordering> findByPackageIdAndMemberId(Long packageId, Long memberId);

    Page<Ordering> findAllByMemberId(Long memberId, Pageable pageable);


}
