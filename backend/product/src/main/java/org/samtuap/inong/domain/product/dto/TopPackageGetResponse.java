package org.samtuap.inong.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;

@Builder
public record TopPackageGetResponse(@NotNull Long id,
                                    @NotNull String packageName,
                                    @NotNull Long farmId,
                                    @NotNull String farmName,
                                    String imageUrl,
                                    @NotNull Long price,
                                    Long orderCount) {

    public static TopPackageGetResponse fromEntity(PackageProduct packageProduct, String thumbnailUrl, Long orderCount) {
        return TopPackageGetResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .farmId(packageProduct.getFarm().getId())
                .farmName(packageProduct.getFarm().getFarmName())
                .imageUrl(thumbnailUrl)
                .price(packageProduct.getPrice())
                .orderCount(orderCount)
                .build();
    }

}
