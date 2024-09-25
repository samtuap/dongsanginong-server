package org.samtuap.inong.domain.farm.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;

@Builder
public record FarmCreateResponse(
        Long id,
        String farmName,
        String bannerImageUrl,
        String profileImageUrl,
        String farmIntro
) {
    public static FarmCreateResponse fromEntity(Farm farm) {
        return FarmCreateResponse.builder()
                .id(farm.getId())
                .farmName(farm.getFarmName())
                .bannerImageUrl(farm.getBannerImageUrl())
                .profileImageUrl(farm.getProfileImageUrl())
                .farmIntro(farm.getFarmIntro())
                .build();
    }
}
