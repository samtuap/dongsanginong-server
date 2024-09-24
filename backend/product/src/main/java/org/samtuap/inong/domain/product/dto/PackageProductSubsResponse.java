package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.product.entity.PackageProduct;

@Builder
public record PackageProductSubsResponse(Long packageId,
                                         String packageName,
                                         String imageUrl,
                                         Long farmId,
                                         String farmName) {

    public static PackageProductSubsResponse fromEntity(PackageProduct packageProduct, String imageUrl, Farm farm){
        return PackageProductSubsResponse.builder()
                .packageId(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .imageUrl(imageUrl)
                .farmId(farm.getId())
                .farmName(farm.getFarmName())
                .build();
    }
}
