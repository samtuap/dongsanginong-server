package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.member.dto.OrderListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderFeign {
    @GetMapping("/order/list")
    List<OrderListResponse> getOrderList(@RequestParam("id") Long memberId);
}
