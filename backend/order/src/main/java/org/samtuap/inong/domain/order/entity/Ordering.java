package org.samtuap.inong.domain.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "UPDATE ordering SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull
    private Long farmId; // farm_id 추가

    private String paymentId; // portone payment id

    public void updatePaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
