package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.chat.dto.SellerInfoResponse;
import org.samtuap.inong.domain.live.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.live.dto.FarmResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface FarmFeign {

    @GetMapping(value = "/farm/{id}")
    FarmResponse getFarmById(@PathVariable("id") Long id);

    @GetMapping("/farm/seller/{sellerId}")
    FarmDetailGetResponse getFarmInfoWithSeller(@PathVariable("sellerId") Long sellerId);

    @GetMapping("/farm/seller/{sellerId}")
    FarmDetailGetResponse getFarmInfoWithSellerId(@PathVariable("sellerId") Long sellerId);

    @GetMapping(value = "/seller/info")
    SellerInfoResponse getSellerInfo(@RequestHeader("sellerId") Long id);

    @GetMapping(value = "/farm/{Id}/sellerId")
    Long getSellerIdByFarmId(@PathVariable("Id") Long farmId);
}
