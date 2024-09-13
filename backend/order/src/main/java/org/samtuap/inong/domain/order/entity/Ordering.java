package org.samtuap.inong.domain.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;


@Entity
@SQLDelete(sql = "UPDATE ordering SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
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
