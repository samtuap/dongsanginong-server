package org.samtuap.inong.domain.live.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FarmDetailGetResponse(@NotNull Long id,
                                    @NotNull Long sellerId,
                                    @NotNull String farmName,
                                    @NotNull String bannerImageUrl,
                                    @NotNull String profileImageUrl,
                                    @NotNull String farmIntro,
                                    @NotNull Long favoriteCount) {
}
