package org.samtuap.inong.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;

@Builder
public record AllPackageListResponse(@NotNull Long id,
                                     @NotNull String packageName,
                                     @NotNull String imageUrl,
                                     @NotNull Long orderCount
                                     ) {
    public static AllPackageListResponse fromEntity(PackageProduct product, String imageUrl, Long orderCount){
        return AllPackageListResponse.builder()
                .id(product.getId())
                .packageName(product.getPackageName())
                .imageUrl(imageUrl)
                .orderCount(orderCount)
                .build();
    }
}
