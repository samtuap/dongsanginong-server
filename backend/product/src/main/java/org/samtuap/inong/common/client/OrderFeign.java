package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderFeign {

    @GetMapping(value = "/order/package/top")
    List<Long> getTopPackages();


    @GetMapping("/order/package/{packageId}/count")
    Long getAllOrders(@PathVariable Long packageId);
}
