package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PackageProductResponse(Long id, // 상품id
                                     String packageName,
                                     Integer delivery_cycle,
                                     Long price,
                                     Long farmId,
                                     String farmName,
                                     List<String> imageUrls)
{
    public static PackageProductResponse from(PackageProductResponse product) {
        return PackageProductResponse.builder()
                .id(product.id)
                .packageName(product.packageName)
                .price(product.price)
                .farmId(product.farmId)
                .farmName(product.farmName)
                .delivery_cycle(product.delivery_cycle)
                .imageUrls(product.imageUrls)
                .build();
    }
}
