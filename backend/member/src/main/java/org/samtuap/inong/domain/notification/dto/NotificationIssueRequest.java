package org.samtuap.inong.domain.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.notification.entity.Notification;

import java.util.List;

@Builder
public record NotificationIssueRequest(@NotNull String title,
                                       @NotNull String content,
                                       @NotNull List<Long> targets) {

    public static Notification of(String title, String content, Member member, String url) {
        return Notification.builder()
                .title(title)
                .content(content)
                .isRead(false)
                .member(member)
                .url(url)
                .build();
    }
}
