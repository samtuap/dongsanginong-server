package org.samtuap.inong.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.notification.entity.Notification;

import java.util.List;

@Builder
public record NotificationGetResponse(Long notificationId,
                                      String title,
                                      String content,
                                      boolean isRead,
                                      String url) {

    public static NotificationGetResponse fromEntity(Notification notification) {
        return NotificationGetResponse.builder()
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .notificationId(notification.getId())
                .url(notification.getUrl())
                .build();
    }
}