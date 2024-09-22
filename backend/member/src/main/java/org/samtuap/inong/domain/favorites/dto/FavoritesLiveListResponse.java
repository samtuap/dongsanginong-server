package org.samtuap.inong.domain.favorites.dto;

public record FavoritesLiveListResponse(
        Long id,
        Long farmId,
        String title,
        String liveImage
) {
}
