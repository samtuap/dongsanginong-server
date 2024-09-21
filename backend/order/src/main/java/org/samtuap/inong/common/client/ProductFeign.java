package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductFeign {

    @GetMapping(value = "/product/info/{id}")
    PackageProductResponse getPackageProduct(@PathVariable("id") Long packageProductId);
}
