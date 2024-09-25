package org.samtuap.inong.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum CancelReason {
    CLIENT_CANCEL("고객 취소"),
    SYSTEM_ERROR("시스템 오류로 인한 취소");

    private String description;
}
