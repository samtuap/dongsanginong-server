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

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE ordering SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Getter
@Builder
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
    private Long farmId; // farm_id 추가

    private String paymentId; // portone payment id

    private LocalDateTime canceledAt;

    @Enumerated(value = EnumType.STRING)
    private CancelReason cancelReason;

    @NotNull
    private boolean isFirst;

    public void updateCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
    public void updateCancelReason(CancelReason cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void updatePaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
