package org.samtuap.inong.domain.notification.dto;

import lombok.Builder;

@Builder
public record NotificationBody(String content, String issuedAt) {
}
