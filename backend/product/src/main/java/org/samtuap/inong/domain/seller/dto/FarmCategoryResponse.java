package org.samtuap.inong.domain.seller.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.FarmCategory;

@Builder
public record FarmCategoryResponse(
        Long id,
        String title
) {

    public static FarmCategoryResponse fromEntity(FarmCategory farmCategory) {
        return FarmCategoryResponse.builder()
                .id(farmCategory.getId())
                .title(farmCategory.getTitle())
                .build();
    }
}
