package org.samtuap.inong.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;

@Builder
public record TopPackageGetResponse(@NotNull Long id,
                                    @NotNull String packageName,
                                    @NotNull Long farmId,
                                    @NotNull String farmName) {

    public static TopPackageGetResponse fromEntity(PackageProduct packageProduct) {
        return TopPackageGetResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .farmId(packageProduct.getFarm().getId())
                .farmName(packageProduct.getFarm().getFarmName())
                .build();
    }

}
