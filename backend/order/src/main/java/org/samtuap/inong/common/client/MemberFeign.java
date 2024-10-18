package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.delivery.dto.MemberDetailResponse;
import org.samtuap.inong.domain.order.dto.MemberAllInfoResponse;
import org.samtuap.inong.domain.order.dto.SubscribeProductRequest;
import org.samtuap.inong.domain.order.dto.SubscriptionInfoGetResponse;
import org.samtuap.inong.domain.order.dto.SubscriptionListGetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "member-service", configuration = FeignConfig.class)
public interface MemberFeign {

    @GetMapping(value = "/member/{id}")
    MemberDetailResponse getMemberById(@PathVariable("id") Long id);

    @GetMapping(value = "/member/all-info/{id}")
    MemberAllInfoResponse getMemberAllInfoById(@PathVariable("id") Long id);

    @GetMapping(value = "/subscription/payment")
    SubscriptionListGetResponse getSubscriptionToPay();


    @PostMapping(value = "/member/info-list")
    List<MemberDetailResponse> getMemberByIds(@RequestBody List<Long> memberIds);

    @PostMapping(value = "/member/info-list-contain-deleted")
    List<MemberDetailResponse> getMemberByIdsContainDeleted(@RequestBody List<Long> memberIds);

    @PostMapping(value = "/subscription")
    ResponseEntity<Void> subscribeProduct(@RequestBody SubscribeProductRequest request);

    @GetMapping(value = "/subscription/product/{productId}")
    Optional<SubscriptionInfoGetResponse> getSubscriptionByProductId(@PathVariable("productId") Long productId, @RequestHeader("myId") Long memberId);
}
