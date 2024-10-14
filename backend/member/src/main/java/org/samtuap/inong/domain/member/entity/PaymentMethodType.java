package org.samtuap.inong.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentMethodType {
    KAKAOPAY("카카오페이");

    private final String paymentMethodValue;
}
