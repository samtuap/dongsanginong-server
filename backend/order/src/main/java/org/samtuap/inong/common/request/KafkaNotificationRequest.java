package org.samtuap.inong.common.request;

import lombok.Builder;

@Builder
public record KafkaNotificationRequest(Long memberId, String title, String content, String url) {
}
