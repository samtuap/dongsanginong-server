package org.samtuap.inong.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TopPackageResponse(@NotNull Long id,
                                 @NotNull String packageName,
                                 @NotNull Long farmId,
                                 @NotNull String farmName) {

}
