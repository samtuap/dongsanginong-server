package org.samtuap.inong.domain.coupon.dto;

import jakarta.validation.constraints.NotNull;

public record FarmSellerResponse(@NotNull Long sellerId) {
}
