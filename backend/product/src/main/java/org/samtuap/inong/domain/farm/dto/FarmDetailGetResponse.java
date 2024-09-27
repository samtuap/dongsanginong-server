package org.samtuap.inong.domain.farm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;

import java.util.Optional;

@Builder
public record FarmDetailGetResponse(@NotNull Long id,
                                    @NotNull Long sellerId,
                                    @NotNull String farmName,
                                    @NotNull String bannerImageUrl,
                                    @NotNull String profileImageUrl,
                                    @NotNull String farmIntro,
                                    @NotNull Long favoriteCount) {

    public static FarmDetailGetResponse fromEntity(Farm farm) {
        return FarmDetailGetResponse.builder()
                .id(farm.getId())
                .sellerId(farm.getSellerId())
                .farmName(farm.getFarmName())
                .bannerImageUrl(farm.getBannerImageUrl())
                .profileImageUrl(farm.getProfileImageUrl())
                .farmIntro(farm.getFarmIntro())
                .favoriteCount(farm.getFavoriteCount())
                .build();
    }
}
