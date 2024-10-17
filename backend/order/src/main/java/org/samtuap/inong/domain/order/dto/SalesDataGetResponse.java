package org.samtuap.inong.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
public record SalesDataGetResponse(Long totalCount,
                                   Long totalSalesAmount,
                                   List<String> labels,
                                   List<Long> monthSaleCount,
                                   List<Long> monthSaleAmount) {
}
