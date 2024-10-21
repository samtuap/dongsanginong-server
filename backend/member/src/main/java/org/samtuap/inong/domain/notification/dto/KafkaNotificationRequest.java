package org.samtuap.inong.domain.notification.dto;

import lombok.Builder;

@Builder
public record KafkaNotificationRequest(Long memberId, String title, String content, String url) {
}
