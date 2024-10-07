package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Builder
public record PackageProductResponse(Long id,
                                     String packageName,
                                     Integer delivery_cycle,
                                     Long price,
                                     Long farmId,
                                     String farmName,
                                     String productDescription,
                                     List<String> imageUrls,
                                     String origin) {
    public static PackageProductResponse fromEntity(PackageProduct packageProduct, List<PackageProductImage> packageProductImage){
        List<String> imageUrls = packageProductImage.stream()
                .map(PackageProductImage::getImageUrl)
                .collect(Collectors.toList());

        return PackageProductResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .delivery_cycle(packageProduct.getDelivery_cycle())
                .price(packageProduct.getPrice())
                .farmId(packageProduct.getFarm().getId())
                .farmName(packageProduct.getFarm().getFarmName())
                .productDescription(packageProduct.getProductDescription())
                .imageUrls(imageUrls)
                .origin(packageProduct.getOrigin())
                .build();
    }
}