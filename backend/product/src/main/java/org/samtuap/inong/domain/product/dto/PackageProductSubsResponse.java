package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;

@Builder
public record PackageProductSubsResponse(String packageName,
                                         String imageUrl) {

    public static PackageProductSubsResponse fromEntity(PackageProduct packageProduct, String imageUrl){
        return PackageProductSubsResponse.builder()
                .packageName(packageProduct.getPackageName())
                .imageUrl(imageUrl)
                .build();
    }
}
