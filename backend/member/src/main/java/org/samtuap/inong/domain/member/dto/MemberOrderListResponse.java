package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberOrderListResponse(Long orderId,
                                      Long packageId,
                                      String packageName,
                                      Long farmId,
                                      String farmName,
                                      String deliveryAt,
                                      String deliveryStatus) {

    public static MemberOrderListResponse from(OrderListResponse order, PackageProductResponse packageProduct){
        return MemberOrderListResponse.builder()
                .orderId(order.orderId())
                .packageId(order.packageId())
                .packageName(packageProduct.packageName())
                .farmId(order.farmId())
                .farmName(packageProduct.farmName())
                .deliveryAt(order.deliveryAt())
                .deliveryStatus(order.deliveryStatus())
                .build();
    }
}
