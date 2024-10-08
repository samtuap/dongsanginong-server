package org.samtuap.inong.domain.notification.dto;

public record KafkaNotificationRequest(Long memberId, String title, String content) {
}
