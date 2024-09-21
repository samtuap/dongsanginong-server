package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record PackageProductResponse(Long id,
                                     String packageName,
                                     Integer delivery_cycle,
                                     Long price,
                                     List<String> imageUrls) {
    public static PackageProductResponse fromEntity(PackageProduct packageProduct, List<PackageProductImage> packageProductImage){
        List<String> imageUrls = packageProductImage.stream()
                .map(PackageProductImage::getImageUrl)
                .collect(Collectors.toList());

        return PackageProductResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .delivery_cycle(packageProduct.getDelivery_cycle())
                .price(packageProduct.getPrice())
                .imageUrls(imageUrls)
                .build();
    }
}
