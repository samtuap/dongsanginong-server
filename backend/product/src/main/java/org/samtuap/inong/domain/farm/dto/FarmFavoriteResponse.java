package org.samtuap.inong.domain.farm.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;

@Builder
public record FarmFavoriteResponse(Long farmId,
                                   String farmName,
                                   String profileImageUrl) {

    public static FarmFavoriteResponse fromEntity(Farm farm){
        return FarmFavoriteResponse.builder()
                .farmId(farm.getId())
                .farmName(farm.getFarmName())
                .profileImageUrl(farm.getProfileImageUrl())
                .build();
    }
}
