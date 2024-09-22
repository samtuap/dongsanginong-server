package org.samtuap.inong.domain.live.dto;

import lombok.Builder;
import org.samtuap.inong.domain.live.entity.Live;

@Builder
public record FavoritesLiveListResponse(
        Long id,
        Long farmId,
        String title,
        String liveImage
) {
    public static FavoritesLiveListResponse from(Live live) {
        return FavoritesLiveListResponse.builder()
                .id(live.getId())
                .farmId(live.getFarmId())
                .title(live.getTitle())
                .liveImage(live.getLiveImage())
                .build();
    }
}
