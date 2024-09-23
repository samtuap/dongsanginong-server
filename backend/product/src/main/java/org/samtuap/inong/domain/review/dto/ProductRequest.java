package org.samtuap.inong.domain.review.dto;

import org.samtuap.inong.domain.product.entity.PackageProduct;

public record ProductRequest(Long id, String packageName, Integer delivery_cycle, Long price) {
    public PackageProduct toEntity() {
        return PackageProduct.builder()
                .id(this.id)
                .packageName(this.packageName)
                .delivery_cycle(this.delivery_cycle)
                .price(this.price)
                .build();
    }
}