package org.samtuap.inong.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentMethod {
    KAKAOPAY("카카오페이");

    private final String paymentType;
}
