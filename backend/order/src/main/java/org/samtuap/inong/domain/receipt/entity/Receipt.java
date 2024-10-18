package org.samtuap.inong.domain.receipt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.order.entity.Ordering;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE receipt SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Receipt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Ordering order;

    private LocalDateTime paidAt;

    private Long beforePrice; // 쿠폰 적용 전
    private Long discountPrice; // 할인 액
    private Long totalPrice; // 최종 결제 금액

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private LocalDateTime refundedAt;

    private String portOnePaymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;


    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void updateRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public void updatePortOnePaymentId(String portOnePaymentId) {
        this.portOnePaymentId = portOnePaymentId;
    }

    public void updatePaymentMethod(PaymentMethodType paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }
}
