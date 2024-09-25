package org.samtuap.inong.domain.delivery.dto;

import lombok.Builder;

@Builder
public record PackageProductResponse(
        Long id,
        String packageName,
        Long farmId,
        String farmName,
        Long price,
        Integer delivery_cycle) {

}