package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.coupon.dto.FarmSellerResponse;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductFeign {

    @GetMapping(value = "/product/info/{id}")
    PackageProductResponse getPackageProduct(@PathVariable("id") Long packageProductId);
    @PostMapping(value = "/product/info")
    List<PackageProductResponse> getPackageProductList(@RequestBody List<Long> ids);

    @GetMapping(value = "/farm/seller/{sellerId}")
    FarmDetailGetResponse getFarmInfoWithSeller(@PathVariable("sellerId") Long sellerId);

    @GetMapping(value = "/farm/seller-by-farm/{farmId}")
    FarmSellerResponse getSellerIdByFarm(@PathVariable("farmId") Long farmId);



}
