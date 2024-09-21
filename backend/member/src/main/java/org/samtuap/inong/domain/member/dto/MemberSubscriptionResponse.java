package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberSubscriptionResponse(String packageName,
                                         Integer deliveryCycle,
                                         Long price,
                                         List<String> imageUrls) {
    public static MemberSubscriptionResponse fromEntity(PackageProductResponse product){
        return MemberSubscriptionResponse.builder()
                .packageName(product.packageName())
                .deliveryCycle(product.delivery_cycle())
                .price(product.price())
                .imageUrls(product.imageUrls())
                .build();
    }
}
