package org.samtuap.inong.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.samtuap.inong.domain.member.entity.PaymentMethodType;

@Builder
public record PaymentMethodGetResponse(PaymentMethodType paymentMethodType,
                                       String paymentMethodValue,
                                       String billingKey,
                                       String logoImageUrl) {
}
