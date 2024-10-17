package org.samtuap.inong.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PackageOrderCount {
    private Long packageId;
    private Long count;
}
