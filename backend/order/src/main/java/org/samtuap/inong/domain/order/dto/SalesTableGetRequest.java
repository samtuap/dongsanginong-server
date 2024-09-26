package org.samtuap.inong.domain.order.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SalesTableGetRequest(LocalDateTime startTime,
                                   LocalDateTime endTime,
                                   boolean onlyFirstSubscription) {
}
