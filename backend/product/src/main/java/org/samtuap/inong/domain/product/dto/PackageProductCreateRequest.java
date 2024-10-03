package org.samtuap.inong.domain.product.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.product.entity.PackageProduct;

import java.util.List;

@Builder
public record PackageProductCreateRequest(
        String packageName,
        Integer deliveryCycle,
        Long price,
        List<String> imageUrls,
        String productDescription
) {

    public static PackageProduct toEntity(Farm farm, PackageProductCreateRequest request) {
        return PackageProduct.builder()
                .packageName(request.packageName)
                .delivery_cycle(request.deliveryCycle)
                .price(request.price)
                .productDescription(request.productDescription)
                .farm(farm)
                .build();
    }
}
