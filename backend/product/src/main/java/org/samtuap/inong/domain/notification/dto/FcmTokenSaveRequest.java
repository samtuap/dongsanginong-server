package org.samtuap.inong.domain.notification.dto;

import jakarta.validation.constraints.NotNull;

public record FcmTokenSaveRequest(@NotNull String fcmToken) {
}
