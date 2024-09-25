package org.samtuap.inong.domain.receipt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum PaymentMethod {
    KAKAOPAY("카카오페이");

    private String detail;

}
