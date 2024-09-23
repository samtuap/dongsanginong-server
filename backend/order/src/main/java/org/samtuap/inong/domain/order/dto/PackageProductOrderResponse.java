package org.samtuap.inong.domain.order.dto;


public record PackageProductOrderResponse(
        Long id,
        String packageName,
        Long price,
        Integer delivery_cycle,
        Long farmId) {

}
