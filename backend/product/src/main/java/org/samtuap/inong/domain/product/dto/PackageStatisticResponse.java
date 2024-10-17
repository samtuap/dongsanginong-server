package org.samtuap.inong.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PackageStatisticResponse {
    private Long id;
    private String packageName;
    private Long amount;
}
