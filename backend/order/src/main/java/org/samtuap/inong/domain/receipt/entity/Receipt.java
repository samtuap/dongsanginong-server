package org.samtuap.inong.domain.receipt.entity;

import jakarta.persistence.*;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.order.entity.Ordering;

import java.time.LocalDateTime;

public class Receipt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Ordering order;

    private LocalDateTime payedAt;

    private Long beforePrice; // 쿠폰 적용 전
    private Long discountPrice; // 할인 액
    private Long totalPrice; // 최종 결제 금액
}
