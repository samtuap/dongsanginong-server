package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberSubsCancelRequest(Long id, // subid
                                      Long packageId,
                                      String packageName,
                                      Integer delivery_cycle,
                                      Long price,
                                      Long farmId,
                                      String imageUrl) {
    public static MemberSubsCancelRequest from(PackageProductResponse packageProductResponse, Long subId){
        List<String> imageUrls = packageProductResponse.imageUrls();
        return MemberSubsCancelRequest.builder()
                .id(subId)
                .packageId(packageProductResponse.id())
                .packageName(packageProductResponse.packageName())
                .delivery_cycle(packageProductResponse.delivery_cycle())
                .price(packageProductResponse.price())
                .farmId(packageProductResponse.farmId())
                .imageUrl(imageUrls.get(0))
                .build();
    }
}
