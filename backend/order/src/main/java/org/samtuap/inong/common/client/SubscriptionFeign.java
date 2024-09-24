package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.delivery.dto.MemberDetailResponse;
import org.samtuap.inong.domain.order.dto.SubscriptionListGetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service", configuration = FeignConfig.class)
public interface SubscriptionFeign {
    @GetMapping(value = "/subscription/payment")
    SubscriptionListGetResponse getSubscriptionToPay();
}
