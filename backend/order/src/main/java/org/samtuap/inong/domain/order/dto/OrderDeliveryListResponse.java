package org.samtuap.inong.domain.order.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.order.entity.Ordering;

@Builder
public record OrderDeliveryListResponse(Long orderId,
                                        Long packageId,
                                        String packageName,
                                        Long farmId,
                                        String farmName,
                                        String deliveryAt,
                                        String deliveryStatus) {

    public static OrderDeliveryListResponse fromEntity(Ordering ordering, PackageProductResponse product, Delivery delivery){
        return OrderDeliveryListResponse.builder()
                .orderId(ordering.getId())
                .packageId(ordering.getPackageId())
                .packageName(product.packageName())
                .farmId(ordering.getFarmId())
                .farmName(product.farmName())
                .deliveryAt(delivery.getDeliveryAt().toString())
                .deliveryStatus(delivery.getDeliveryStatus().toString())
                .build();
    }
}
