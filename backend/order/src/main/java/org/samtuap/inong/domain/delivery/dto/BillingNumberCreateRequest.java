package org.samtuap.inong.domain.delivery.dto;

import lombok.Builder;

@Builder
public record BillingNumberCreateRequest(
        Long id,
        String billingNumber
) {

}
