package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.product.entity.PackageProduct;

@Builder
public record PackageProductForSaleListResponse(Long packageId,
                                                String packageName,
                                                String imageUrl,
                                                Long farmId,
                                                String farmName,
                                                Integer deliveryCycle,
                                                Long price) {

    public static PackageProductForSaleListResponse fromEntity(PackageProduct packageProduct, String imageUrl, Farm farm){
        return PackageProductForSaleListResponse.builder()
                .packageId(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .imageUrl(imageUrl)
                .farmId(farm.getId())
                .farmName(farm.getFarmName())
                .deliveryCycle(packageProduct.getDelivery_cycle())
                .price(packageProduct.getPrice())
                .build();
    }
}
