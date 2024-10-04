package org.samtuap.inong.domain.live.dto;

import io.openvidu.java.client.Session;
import lombok.Builder;
import org.samtuap.inong.domain.live.entity.Live;

@Builder
public record LiveSessionResponse(
        Long liveId,
        String sessionId,
        String title,
        String liveImage,
        boolean isLive
) {
    public static LiveSessionResponse fromEntity(LiveSessionRequest request, Live live, Session session) {
        return LiveSessionResponse.builder()
                .liveId(live.getId())
                .sessionId(session.getSessionId())
                .title(request.title())
                .liveImage(request.liveImage())
                .isLive(true)
                .build();
    }
}
