package org.samtuap.inong.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TopPackageResponse(Long packageId,
                                 Long orderCount) {

}
