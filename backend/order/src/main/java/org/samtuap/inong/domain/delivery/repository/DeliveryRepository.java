package org.samtuap.inong.domain.delivery.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.delivery.entity.DeliveryStatus;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static org.samtuap.inong.common.exceptionType.DeliveryExceptionType.DELIVERY_NOT_FOUND;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Page<Delivery> findByOrderingInAndDeliveryStatusAndDeliveryDueDateBefore(List<Ordering> orderList,
                                                                             DeliveryStatus deliveryStatus,
                                                                             LocalDate endDate,
                                                                             Pageable pageable);

    default Delivery findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(()->new BaseCustomException(DELIVERY_NOT_FOUND));
    }

    Page<Delivery> findByOrderingInAndDeliveryStatusIn(List<Ordering> orderList,
                                                       List<DeliveryStatus> statuses,
                                                       Pageable pageable);

    List<Delivery> findAllByOrdering(Ordering ordering);
}
