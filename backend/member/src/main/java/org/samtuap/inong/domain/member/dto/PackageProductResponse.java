package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PackageProductResponse(Long id,
                                     String packageName,
                                     Integer delivery_cycle,
                                     Long price,
                                     Long farmId,
                                     String farmName,
                                     List<String> imageUrls) {
}
