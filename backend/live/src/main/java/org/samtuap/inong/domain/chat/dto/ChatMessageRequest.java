package org.samtuap.inong.domain.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageRequest(
        Long memberId,
        Long liveId,
        String name,
        String content,
        MessageType type
) {
}
