package org.samtuap.inong.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentMethodType {
    KAKAOPAY("카카오페이", "https://dongsanginong-bucket.s3.ap-northeast-2.amazonaws.com/local/kakaopay.png");

    private final String paymentMethodValue;
    private final String logoImageUrl;
}
