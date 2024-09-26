package org.samtuap.inong.domain.farm.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;

import java.util.List;

@Builder
public record FarmCreateRequest(
        String farmName,
        String bannerImageUrl,
        String profileImageUrl,
        String farmIntro,
        List<Long> categories
) {
    public static Farm toEntity(FarmCreateRequest request, Long sellerId, String bannerImageUrl, String profileImageUrl) {
        return Farm.builder()
                .sellerId(sellerId)
                .farmName(request.farmName())
                .bannerImageUrl(bannerImageUrl)
                .profileImageUrl(profileImageUrl)
                .farmIntro(request.farmIntro())
                .favoriteCount(0L)
                .orderCount(0L)
                .build();
    }
}
