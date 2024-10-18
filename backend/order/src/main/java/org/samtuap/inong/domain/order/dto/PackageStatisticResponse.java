package org.samtuap.inong.domain.order.dto;

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