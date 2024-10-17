package org.samtuap.inong.common.client;


import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.member.dto.FarmFavoriteResponse;
import org.samtuap.inong.domain.favorites.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.samtuap.inong.domain.subscription.dto.PackageProductListGetRequest;
import org.samtuap.inong.domain.subscription.dto.PackageProductListGetResponse;
import org.samtuap.inong.domain.member.dto.PackageProductSubsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductFeign {
    @GetMapping("/product/info/{id}")
    PackageProductResponse getPackageProduct(@PathVariable("id") Long packageProductId);

    @PostMapping("/product/subscription/list")
    List<PackageProductSubsResponse> getProductSubsList(@RequestBody List<Long> subscriptionIds);

    @PostMapping("/farm/favorite/list")
    List<FarmFavoriteResponse> getFarmFavoriteList(@RequestBody List<Long> farmFavoriteIds);

    @PostMapping("/farm/favorites/list")
    List<FavoritesLiveListResponse> getFavoritesFarmLiveList(@RequestBody List<Long> farmFavoriteIds);

    @GetMapping("/product/info")
    List<PackageProductResponse> getPackageProductList(@RequestBody PackageProductListGetRequest reqDto);

    @PostMapping("/farm/{farmId}/decrease-like")
    void decreaseLike(@PathVariable("farmId") Long farmId);

    @PostMapping("/farm/{farmId}/increase-like")
    void increaseLike(@PathVariable("farmId") Long farmId);

    @PostMapping("/product/{packageProductId}/increase-wish")
    void increaseWish(@PathVariable("packageProductId") Long packageProductId);

    @PostMapping("/product/{packageProductId}/decrease-wish")
    void decreaseWish(@PathVariable("packageProductId") Long packageProductId);
}
