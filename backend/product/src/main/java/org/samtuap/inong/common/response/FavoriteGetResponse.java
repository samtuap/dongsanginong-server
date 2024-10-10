package org.samtuap.inong.common.response;

import lombok.Builder;
import lombok.Getter;

@Builder
public record FavoriteGetResponse(Long favoriteId, Long memberId, Long farmId) {

}
