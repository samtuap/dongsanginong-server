package org.samtuap.inong.domain.chat.dto;

import lombok.Builder;
import org.samtuap.inong.domain.chat.entity.Chat;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponse(
        Long memberId,
        Long liveId,
        String content,
        LocalDateTime timestamp,
        MessageType type
) {
    // fromEntity 메서드를 통해 엔티티에서 Response로 변환
    public static ChatMessageResponse fromEntity(Chat chat) {
        return ChatMessageResponse.builder()
                .memberId(chat.getMemberId())
                .liveId(chat.getLive().getId())
                .content(chat.getContent())
                .timestamp(chat.getCreatedAt())
                .build();
    }
}
