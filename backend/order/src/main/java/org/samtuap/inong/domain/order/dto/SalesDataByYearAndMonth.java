package org.samtuap.inong.domain.order.dto;

import lombok.*;

@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SalesDataByYearAndMonth {
    private Long count;
    private Long amount;
}
