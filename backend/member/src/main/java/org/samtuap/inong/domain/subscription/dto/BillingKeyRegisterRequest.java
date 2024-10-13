package org.samtuap.inong.domain.subscription.dto;

import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.member.entity.PaymentMethod;

public record BillingKeyRegisterRequest(@NotNull String billingKey,
                                        @NotNull PaymentMethod paymentMethod) {
}
