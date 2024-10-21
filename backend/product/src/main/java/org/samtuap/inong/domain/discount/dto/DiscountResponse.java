package org.samtuap.inong.domain.discount.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DiscountResponse {
    private Long id;
    private Integer discount;
    private LocalDate startAt;
    private LocalDate endAt;
    private boolean discountActive;
    private Long packageProductId;

    public DiscountResponse(Long id, Integer discount, LocalDate startAt, LocalDate endAt, boolean discountActive, Long packageProductId) {
        this.id = id;
        this.discount = discount;
        this.startAt = startAt;
        this.endAt = endAt;
        this.discountActive = discountActive;
        this.packageProductId = packageProductId;
    }
}
