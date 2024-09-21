package org.samtuap.inong.common.client;


import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductFeign {
    @GetMapping("/product/info")
    PackageProductResponse getPackageProduct(@RequestParam("id") Long packageProductId);
}
