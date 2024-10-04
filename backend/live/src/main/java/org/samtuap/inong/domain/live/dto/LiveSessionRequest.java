package org.samtuap.inong.domain.live.dto;

import lombok.Builder;

@Builder
public record LiveSessionRequest(
        Long sessionId,
        String title,
        String liveImage
) {
}
