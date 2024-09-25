package org.samtuap.inong.domain.order.repository;


import org.samtuap.inong.domain.order.dto.TopPackageResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    List<Ordering> findAllByMemberId(Long memberId);

}
