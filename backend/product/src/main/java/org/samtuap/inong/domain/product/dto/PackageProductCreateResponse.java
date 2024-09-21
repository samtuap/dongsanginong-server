package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PackageProductCreateResponse(
        Long id,
        String packageName,
        Integer deliveryCycle,
        Long price,
        String farmName,
        List<String> imageUrls,
        LocalDateTime createdAt
) {

    public static PackageProductCreateResponse fromEntity(PackageProduct packageProduct, List<String> imageUrls) {
        return PackageProductCreateResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .deliveryCycle(packageProduct.getDelivery_cycle())
                .price(packageProduct.getPrice())
                .farmName(packageProduct.getFarm().getFarmName())
                .imageUrls(imageUrls)
                .createdAt(packageProduct.getCreatedAt())
                .build();
    }
}
