package org.samtuap.inong.domain.favorites.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record FavoriteGetResponse(Long favoriteId, Long memberId, Long farmId) {

}
