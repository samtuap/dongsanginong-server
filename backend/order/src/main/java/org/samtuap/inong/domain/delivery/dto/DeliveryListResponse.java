package org.samtuap.inong.domain.delivery.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.order.entity.Ordering;

import java.util.List;

@Builder
public record DeliveryListResponse(Long deliveryId,
                                   Long packageId,
                                   String packageName,
                                   String farmName,
                                   String deliveryAt, // 여러 개의 배송 시작 시점
                                   String deliveryStatus) { // 여러 개의 배송 상태

    public static DeliveryListResponse fromEntity(PackageProductResponse product, Delivery delivery) {
        return DeliveryListResponse.builder()
                .deliveryId(delivery.getId())
                .packageId(product.id())
                .packageName(product.packageName())
                .farmName(product.farmName())
                .deliveryAt(String.valueOf(delivery.getDeliveryAt()))
                .deliveryStatus(String.valueOf(delivery.getDeliveryStatus()))
                .build();
    }
}