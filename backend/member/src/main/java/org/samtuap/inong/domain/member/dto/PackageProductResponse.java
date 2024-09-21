package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record PackageProductResponse(Long id,
                                     String packageName,
                                     Integer delivery_cycle,
                                     Long price,
                                     String imageUrl) {
}
