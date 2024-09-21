package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;

@Builder
public record PackageProductResponse(Long id,
                                     String packageName,
                                     Integer delivery_cycle,
                                     Long price,
                                     String imageUrl) {

    public static PackageProductResponse fromEntity(PackageProduct packageProduct, PackageProductImage packageProductImage){
        return PackageProductResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .delivery_cycle(packageProduct.getDelivery_cycle())
                .price(packageProduct.getPrice())
                .imageUrl(packageProductImage.getImageUrl())
                .build();
    }
}
