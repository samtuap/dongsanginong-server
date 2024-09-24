package org.samtuap.inong.domain.order.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentResponse(Long orderId,
                              LocalDateTime createdAt) {
}
