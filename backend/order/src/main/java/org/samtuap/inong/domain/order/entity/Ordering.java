package org.samtuap.inong.domain.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;


@Entity
public class Ordering extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long packageId;

    @NotNull
    private Long totalPrice;

    @NotNull
    private Long discountPrice;
}
