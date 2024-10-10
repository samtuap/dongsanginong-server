package org.samtuap.inong.domain.farm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;

@Builder
public record FarmListGetResponse(@NotNull Long id,
                                  @NotNull String farmName,
                                  @NotNull String imageUrl,
                                  @NotNull Long favoriteCount,
                                  @NotNull Long orderCount,
                                  @NotNull Boolean isLiked
                                  ) {

    public static FarmListGetResponse fromEntity(Farm farm, boolean isLiked) {
        return FarmListGetResponse.builder()
                .id(farm.getId())
                .farmName(farm.getFarmName())
                .imageUrl(farm.getProfileImageUrl())
                .favoriteCount(farm.getFavoriteCount())
                .orderCount(farm.getOrderCount())
                .isLiked(isLiked)
                .build();
    }

}