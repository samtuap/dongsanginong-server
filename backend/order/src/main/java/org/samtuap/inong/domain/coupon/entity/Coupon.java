package org.samtuap.inong.domain.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;


@Entity
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String couponName;

    @NotNull
    private Integer discountPercentage;

    @NotNull
    private LocalDateTime expiration;

}
