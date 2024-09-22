package org.samtuap.inong.domain.delivery.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.entity.Delivery;

@Builder
public record DeliveryCompletedListResponse(
        Long id,
        String memberName,
        String packageProductName,
        String deliveryAt,
        String billingNumber,
        String deliveryStatus
) {
    public static DeliveryCompletedListResponse from(Delivery delivery, String memberName, String packageProductName) {
        return DeliveryCompletedListResponse.builder()
                .id(delivery.getId())
                .memberName(memberName)
                .packageProductName(packageProductName)
                .deliveryAt(String.valueOf(delivery.getDeliveryAt()))
                .billingNumber(delivery.getBillingNumber())
                .deliveryStatus(String.valueOf(delivery.getDeliveryStatus()))
                .build();
    }
}
