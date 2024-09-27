package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.farmNotice.dto.FollowersGetResponse;
import org.samtuap.inong.domain.farmNotice.dto.MemberDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "member-service", configuration = FeignConfig.class)
public interface MemberFeign {

    @GetMapping(value = "/member/{id}")
    MemberDetailResponse getMemberById(@PathVariable("id") Long id);


    @GetMapping(value = "/favorites/farm/{farmId}/followers")
    FollowersGetResponse getFollowers(@PathVariable("farmId") Long farmId);
    @PostMapping(value = "/member/info-list")
    List<MemberDetailResponse> getMemberByIds(@RequestBody List<Long> memberIds);
}