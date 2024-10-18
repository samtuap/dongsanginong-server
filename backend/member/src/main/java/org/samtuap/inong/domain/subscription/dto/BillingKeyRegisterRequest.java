package org.samtuap.inong.domain.subscription.dto;

import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.member.entity.PaymentMethodType;

public record BillingKeyRegisterRequest(@NotNull String billingKey,
                                        @NotNull PaymentMethodType paymentMethodType) {
}
