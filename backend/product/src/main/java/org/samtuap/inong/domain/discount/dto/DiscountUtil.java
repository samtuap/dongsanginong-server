package org.samtuap.inong.domain.discount.dto;

import org.samtuap.inong.domain.discount.entity.Discount;

public class DiscountUtil {

    // 할인 활성화 메서드
    public static void activateDiscount(Discount discount) {
        discount.setDiscountActive(true);
    }

    // 할인 비활성화 메서드
    public static void deactivateDiscount(Discount discount) {
        discount.setDiscountActive(false);
    }
}
