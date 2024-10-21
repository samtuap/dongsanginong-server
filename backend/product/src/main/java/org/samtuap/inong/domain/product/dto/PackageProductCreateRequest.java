package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.product.entity.PackageProduct;

import java.util.List;
import java.util.UUID;

@Builder
public record PackageProductCreateRequest(
        String packageName,
        Integer deliveryCycle,
        Long price,
        List<String> imageUrls,
        String productDescription,
        String origin,
        Long wishCount
) {

    public static PackageProduct toEntity(Farm farm, PackageProductCreateRequest request) {
        return PackageProduct.builder()
                .packageName(request.packageName)
                .delivery_cycle(request.deliveryCycle)
                .price(request.price)
                .productDescription(request.productDescription)
                .farm(farm)
                .productCode("P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .origin(request.origin)
                .wishCount(0L)
                .build();
    }
}
