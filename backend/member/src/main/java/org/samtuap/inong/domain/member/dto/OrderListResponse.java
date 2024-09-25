package org.samtuap.inong.domain.member.dto;

public record OrderListResponse(Long orderId,
                                Long packageId,
                                Long farmId,
                                String deliveryAt,
                                String deliveryStatus) {
}
