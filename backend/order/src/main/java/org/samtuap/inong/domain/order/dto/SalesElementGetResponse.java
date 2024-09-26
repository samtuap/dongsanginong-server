package org.samtuap.inong.domain.order.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

// 매출 내역 단건 => Page로 반환
public record SalesElementGetResponse(Long orderId,
                                      LocalDateTime paidAt,
                                      Long packageId,
                                      String packageName,
                                      Long customerId,
                                      String customerName,
                                      Integer count,
                                      Long amount) {
}
