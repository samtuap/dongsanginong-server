package org.samtuap.inong.domain.chat.dto;

import lombok.Builder;

@Builder
public record KickMessage(
        Long memberId,
        String message
) {
}