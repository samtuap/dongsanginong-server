package org.samtuap.inong.domain.order.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.order.entity.Ordering;

import java.util.List;

@Builder
public record OrderDeliveryListResponse(Long orderId,
                                        Long packageId,
                                        String packageName,
                                        Long farmId,
                                        String farmName,
                                        List<String> deliveryAt, // 여러 개의 배송 시작 시점
                                        List<String> deliveryStatus) { // 여러 개의 배송 상태

    public static OrderDeliveryListResponse fromEntity(Ordering ordering, PackageProductResponse product, List<Delivery> deliveries) {
        List<String> deliveryAtList = deliveries.stream()
                .map(delivery -> delivery.getDeliveryAt() != null ? delivery.getDeliveryAt().toString() : null)
                .toList(); // 각 배송의 시작 시점을 리스트로 변환

        List<String> deliveryStatusList = deliveries.stream()
                .map(delivery -> delivery.getDeliveryStatus() != null ? delivery.getDeliveryStatus().toString() : null)
                .toList(); // 각 배송 상태를 리스트로 변환

        return OrderDeliveryListResponse.builder()
                .orderId(ordering.getId())
                .packageId(ordering.getPackageId())
                .packageName(product.packageName())
                .farmId(ordering.getFarmId())
                .farmName(product.farmName())
                .deliveryAt(deliveryAtList) // 리스트로 전달
                .deliveryStatus(deliveryStatusList) // 리스트로 전달
                .build();
    }
}