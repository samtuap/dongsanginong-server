package org.samtuap.inong.domain.delivery.repository;

import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.delivery.entity.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Page<Delivery> findByDeliveryStatusAndDeliveryAtBefore(DeliveryStatus deliveryStatus,
                                                           LocalDateTime endDate,
                                                           Pageable pageable);
}
