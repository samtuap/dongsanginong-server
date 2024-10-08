package org.samtuap.inong.domain.farm.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategory;

import java.util.List;

@Builder
public record FarmInfoResponse(Long id,
                               String farmName,
                               String bannerImageUrl,
                               String profileImageUrl,
                               String farmIntro,
                               List<Long> categories) {
    public static FarmInfoResponse fromEntity(Farm farm, List<Long> categories) {
        return FarmInfoResponse.builder()
                .id(farm.getId())
                .farmName(farm.getFarmName())
                .bannerImageUrl(farm.getBannerImageUrl())
                .profileImageUrl(farm.getProfileImageUrl())
                .farmIntro(farm.getFarmIntro())
                .categories(categories)
                .build();
    }
}
