package org.samtuap.inong.domain.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;

import static org.samtuap.inong.common.exceptionType.CouponExceptionType.COUPON_SOLD_OUT;

@Entity
@SQLDelete(sql = "UPDATE coupon SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull
    private Integer quantity; // 수량이 -1일 경우 무제한 발급

    @NotNull
    private Long farmId; // 농장 id

    public void decreaseQuantity() {
        if (this.quantity == -1) {
            // 무제한 발급이므로 수량을 감소시키지 않음
            return;
        }
        if (this.quantity <= 0) {
            throw new BaseCustomException(COUPON_SOLD_OUT);
        }
        this.quantity--;
    }

}
