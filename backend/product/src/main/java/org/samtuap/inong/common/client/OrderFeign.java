package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderFeign {

    @GetMapping(value = "/order/package/top")
    List<Long> getTopPackages();


    @PostMapping("/order/counts")
    Long getAllOrders(@RequestBody Long packageId);
}
