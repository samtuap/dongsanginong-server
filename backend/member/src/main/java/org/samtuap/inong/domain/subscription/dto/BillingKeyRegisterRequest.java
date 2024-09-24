package org.samtuap.inong.domain.subscription.dto;

import jakarta.validation.constraints.NotNull;

public record BillingKeyRegisterRequest(@NotNull String billingKey) {
}
