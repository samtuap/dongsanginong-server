package org.samtuap.inong.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record NotificationIssueRequest(@NotNull String title,
                                       @NotNull String content,
                                       @NotNull List<Long> targets) {
}
