package org.samtuap.inong.domain.discount.dto;

import lombok.Getter;
import lombok.Setter;
import org.samtuap.inong.domain.discount.entity.Discount;

import java.time.LocalDate;

@Getter
@Setter
public class DiscountUpdateRequest {
    private Integer discount;
    private LocalDate startAt;
    private LocalDate endAt;
    private boolean discountActive;

    public DiscountUpdateRequest(Integer discount, LocalDate startAt, LocalDate endAt, boolean discountActive) {
        this.discount = discount;
        this.startAt = startAt;
        this.endAt = endAt;
        this.discountActive = discountActive;
    }

    public void updateEntity(Discount discount) {
        discount.setDiscount(this.discount);
        discount.setStartAt(this.startAt);
        discount.setEndAt(this.endAt);
        discount.setDiscountActive(this.discountActive);
    }
}
