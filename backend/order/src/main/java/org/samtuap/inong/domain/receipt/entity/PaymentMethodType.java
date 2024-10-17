package org.samtuap.inong.domain.receipt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum PaymentMethodType {
    KAKAOPAY("카카오페이", "https://dongsanginong-bucket.s3.ap-northeast-2.amazonaws.com/local/kakaopay.png");

    private String detail;
    private String logoImageUrl;

}
