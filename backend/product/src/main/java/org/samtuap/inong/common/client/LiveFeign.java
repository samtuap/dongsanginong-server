package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.farm.dto.FavoritesLiveListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "live-service", configuration = FeignConfig.class)
public interface LiveFeign {

    @PostMapping("/live/farm")
    List<FavoritesLiveListResponse> getFavoritesFarmLiveList(@RequestBody List<Long> favoriteFarmList);
}
