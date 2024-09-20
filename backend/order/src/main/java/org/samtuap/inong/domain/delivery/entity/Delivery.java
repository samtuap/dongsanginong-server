package org.samtuap.inong.domain.delivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.order.entity.Ordering;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE coupon SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Delivery extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String courier; // 택배사

    @NotNull
    private String billing_number; // 운송장번호

    @NotNull
    private LocalDateTime delivery_at; // 배송 시작 시점

    @NotNull
    @Enumerated(EnumType.STRING)
    private DeliveryStatus delivery_status; // 배송 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering;

}
