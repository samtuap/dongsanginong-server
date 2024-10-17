package org.samtuap.inong.domain.order.dto;

import java.util.List;

public record SalesDataWithPackagesGetResponse(List<String> labels, List<Long> count) {

}
