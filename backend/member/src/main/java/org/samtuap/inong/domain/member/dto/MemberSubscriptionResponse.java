package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberSubscriptionResponse(String packageName,
                                         Integer deliveryCycle,
                                         Long price,
                                         String imageUrl) {
    public static MemberSubscriptionResponse fromEntity(PackageProductResponse product){
        return MemberSubscriptionResponse.builder()
                .packageName(product.packageName())
                .deliveryCycle(product.delivery_cycle())
                .price(product.price())
                .imageUrl(product.imageUrl())
                .build();
    }
}
