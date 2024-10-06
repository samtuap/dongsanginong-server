package org.samtuap.inong.domain.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageRequest(
        Long memberId,
        Long sellerId,
        String liveId,
        String name,
        String content,
        boolean isSeller,
        MessageType type
) {
}