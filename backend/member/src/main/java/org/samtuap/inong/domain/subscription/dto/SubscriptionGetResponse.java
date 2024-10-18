package org.samtuap.inong.domain.subscription.dto;

import lombok.Builder;
import org.samtuap.inong.domain.subscription.entity.Subscription;

import java.time.LocalDate;

@Builder
public record SubscriptionGetResponse(Long productId, Long memberId, LocalDate payDate) {

    public static SubscriptionGetResponse fromEntity(Subscription subscription) {
        return SubscriptionGetResponse.builder()
                .productId(subscription.getPackageId())
                .memberId(subscription.getMember().getId())
                .payDate(subscription.getPayDate())
                .build();
    }
}
