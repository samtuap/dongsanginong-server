package org.samtuap.inong.common.client;


import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.favorites.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductFeign {
    @GetMapping("/product/info/{id}")
    PackageProductResponse getPackageProduct(@PathVariable("id") Long packageProductId);

    @PostMapping("/farm/favorites/list")
    List<FavoritesLiveListResponse> getFavoritesFarmLiveList(@RequestBody List<Long> favoriteFarmList);
}
