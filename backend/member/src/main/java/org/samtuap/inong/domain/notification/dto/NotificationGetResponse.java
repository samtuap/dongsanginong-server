package org.samtuap.inong.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.notification.entity.Notification;

import java.util.List;

@Builder
public record NotificationGetResponse(String title,
                                      String content,
                                      boolean isRead) {

    public static NotificationGetResponse fromEntity(Notification notification) {
        return NotificationGetResponse.builder()
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .build();
    }
}