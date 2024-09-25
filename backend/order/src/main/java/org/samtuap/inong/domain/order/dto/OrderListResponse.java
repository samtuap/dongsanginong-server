package org.samtuap.inong.domain.order.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.order.entity.Ordering;

@Builder
public record OrderListResponse(Long orderId,
                                Long packageId,
                                Long farmId,
                                String deliveryAt,
                                String deliveryStatus) {

    public static OrderListResponse fromEntity(Ordering ordering, Delivery delivery){
        return OrderListResponse.builder()
                .orderId(ordering.getId())
                .packageId(ordering.getPackageId())
                .farmId(ordering.getFarmId())
                .deliveryAt(delivery.getDeliveryAt().toString())
                .deliveryStatus(delivery.getDeliveryStatus().toString())
                .build();
    }
}
