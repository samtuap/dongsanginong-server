package org.samtuap.inong.domain.delivery.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.entity.Delivery;

@Builder
public record DeliveryUpComingListResponse(
        Long id,
        String memberName,
        String packageProductName,
        String deliveryDueDate
        // 다가오는 배송에서 '운송장 번호'는 빈값이므로 dto에서 제외
) {

    public static DeliveryUpComingListResponse from(Delivery delivery, String memberName, String packageProductName) {
        return DeliveryUpComingListResponse.builder()
                .id(delivery.getId())
                .memberName(memberName)
                .packageProductName(packageProductName)
                .deliveryDueDate(String.valueOf(delivery.getDeliveryDueDate()))
                .build();
    }
}