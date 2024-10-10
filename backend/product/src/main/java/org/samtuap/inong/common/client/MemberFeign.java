package org.samtuap.inong.common.client;

import org.samtuap.inong.common.response.FavoriteGetResponse;
import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.farmNotice.dto.FollowersGetResponse;
import org.samtuap.inong.domain.farmNotice.dto.MemberDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "member-service", configuration = FeignConfig.class)
public interface MemberFeign {

    @GetMapping(value = "/member/{id}")
    MemberDetailResponse getMemberById(@PathVariable("id") Long id);


    @GetMapping(value = "/favorites/farm/{farmId}/followers")
    FollowersGetResponse getFollowers(@PathVariable("farmId") Long farmId);
    @PostMapping(value = "/member/info-list")
    List<MemberDetailResponse> getMemberByIds(@RequestBody List<Long> memberIds);


    @GetMapping(value = "/favorites/farm/{farmId}")
    FavoriteGetResponse getFavorite(@RequestParam(value = "memberId", required = false) Long memberId, @PathVariable(value = "farmId") Long farmId);
}