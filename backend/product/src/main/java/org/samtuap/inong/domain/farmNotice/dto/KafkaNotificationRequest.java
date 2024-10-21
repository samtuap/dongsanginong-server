package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;

@Builder
public record KafkaNotificationRequest(Long memberId, String title, String content, String url) {
}
