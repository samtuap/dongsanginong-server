package org.samtuap.inong.domain.order.dto;

import java.time.LocalDate;

public record SubscriptionInfoGetResponse(Long productId, Long memberId, LocalDate payDate) {
}
