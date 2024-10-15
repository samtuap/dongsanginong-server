package org.samtuap.inong.domain.order.dto;

public record KafkaOrderCountUpdateRequest(Long farmId, OrderCountRequestType orderCountRequestType) {
    public static enum OrderCountRequestType {
        INCREASE, DECREASE;
    }
}
