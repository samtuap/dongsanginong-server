package org.samtuap.inong.domain.live.dto;

import lombok.Builder;
import org.samtuap.inong.domain.live.entity.Live;

@Builder
public record FavoritesLiveListResponse(
        Long id,
        String sessionId,
        Long farmId,
        String title,
        String liveImage,
        int participantCount
) {
    public static FavoritesLiveListResponse from(Live live, int participantCount) {
        return FavoritesLiveListResponse.builder()
                .id(live.getId())
                .sessionId(live.getSessionId())
                .farmId(live.getFarmId())
                .title(live.getTitle())
                .liveImage(live.getLiveImage())
                .participantCount(participantCount)
                .build();
    }
}
