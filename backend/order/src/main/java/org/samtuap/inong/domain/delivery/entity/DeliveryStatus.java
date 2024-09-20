package org.samtuap.inong.domain.delivery.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryStatus {

    BEFORE_DELIVERY("배송전"),
    IN_DELIVERY("배송중"),
    AFTER_DELIVERY("배송완료");

    private String description;
}
