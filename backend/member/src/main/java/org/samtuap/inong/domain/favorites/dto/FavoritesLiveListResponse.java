package org.samtuap.inong.domain.favorites.dto;

public record FavoritesLiveListResponse(
        Long id,
        String sessionId,
        Long farmId,
        String farmName,
        String title,
        String liveImage,
        int participantCount
) {
}
