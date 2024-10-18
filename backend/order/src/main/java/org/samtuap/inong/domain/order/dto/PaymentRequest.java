package org.samtuap.inong.domain.order.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(@NotNull Long packageId, Long couponId) {
}
