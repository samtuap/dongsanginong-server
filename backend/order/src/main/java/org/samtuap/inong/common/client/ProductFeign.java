package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.coupon.dto.FarmSellerResponse;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.dto.PackageStatisticResponse;
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


    // TODO: 아직 구현 안됨. 추후 구현 예정. 삭제가 된 상품까지 다 들고 오기
    @PostMapping(value = "/product/info/contain-deleted")
    List<PackageProductResponse> getPackageProductListContainDeleted(@RequestBody List<Long> ids);

    @PostMapping("/product/info/contain-deleted/name-only")
    List<PackageStatisticResponse> getPackageProductListContainDeletedNameOnly(@RequestBody List<Long> ids);

}
