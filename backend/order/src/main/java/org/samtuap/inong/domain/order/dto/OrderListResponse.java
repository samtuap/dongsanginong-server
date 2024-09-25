package org.samtuap.inong.domain.order.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.order.entity.Ordering;

@Builder
public record OrderListResponse(Long orderId,
                                Long packageId,
                                String packageName,
                                Long farmId,
                                String farmName,
                                String deliveryAt,
                                String deliveryStatus) {

    public static OrderListResponse fromEntity(Ordering ordering, PackageProductResponse product, Delivery delivery){
        return OrderListResponse.builder()
                .orderId(ordering.getId())
                .packageId(ordering.getPackageId())
                .packageName(product.packageName())
                .farmId(ordering.getFarmId())
                .farmName(product.packageName())
                .deliveryAt(delivery.getDeliveryAt().toString())
                .deliveryStatus(delivery.getDeliveryStatus().toString())
                .build();
    }
}
