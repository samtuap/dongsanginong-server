package org.samtuap.inong.domain.delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.samtuap.inong.domain.delivery.entity.DeliveryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE coupon SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String courier; // 택배사

    private String billingNumber; // 운송장번호 => 배송 시점에 생기므로 null 가능

    private LocalDateTime deliveryAt; // 배송 시작 시점 (운송장 번호 등록)

    @NotNull
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus; // 배송 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering;

    @NotNull
    private LocalDate deliveryDueDate; // 배송 '예정' 날짜

    public void updateDelivery(String billingNumber, DeliveryStatus deliveryStatus, LocalDateTime now) {
        this.billingNumber = billingNumber;
        this.deliveryStatus = deliveryStatus;
        this.deliveryAt = now;
    }

}
