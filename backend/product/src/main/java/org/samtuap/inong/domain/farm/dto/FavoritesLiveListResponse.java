package org.samtuap.inong.domain.farm.dto;

public record FavoritesLiveListResponse(
        Long id,
        Long farmId,
        String title,
        String liveImage
) {
}
