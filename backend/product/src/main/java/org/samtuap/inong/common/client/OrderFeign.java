package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.product.dto.TopPackageGetResponse;
import org.samtuap.inong.domain.product.dto.TopPackageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderFeign {

    @GetMapping(value = "/order/package/top")
    List<Long> getTopPackages();

}
