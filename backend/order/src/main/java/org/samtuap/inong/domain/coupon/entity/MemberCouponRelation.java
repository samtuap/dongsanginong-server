package org.samtuap.inong.domain.coupon.entity;

import jakarta.persistence.*;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
public class MemberCouponRelation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Long orderId;
    private Long memberId;
    private String useYn;
    private LocalDateTime usedAt;
    private LocalDateTime issuedAt;
}
