package org.samtuap.inong.domain.farm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;

@Builder
public record FarmSellerResponse(@NotNull Long sellerId) {

    public static FarmSellerResponse fromEntity(Farm farm) {
        return FarmSellerResponse.builder()
                .sellerId(farm.getSellerId())
                .build();
    }
}
