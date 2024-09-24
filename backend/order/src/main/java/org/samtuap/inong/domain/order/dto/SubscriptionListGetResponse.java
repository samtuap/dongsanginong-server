package org.samtuap.inong.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;

import java.util.List;

public record SubscriptionListGetResponse(List<SubscriptionGetResponse> subscriptions) {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscriptionGetResponse {
        private Long packageId;
        private Long memberId;
    }
}
