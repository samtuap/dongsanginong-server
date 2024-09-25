package org.samtuap.inong.domain.receipt.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PaymentStatus {
    PAID("정상 결제 완료"),
    REFUND_PROCESSING("환불 처리 중"),
    REFUNDED("환불 완료");

    private String description;
}
