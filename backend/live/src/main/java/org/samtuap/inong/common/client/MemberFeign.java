package org.samtuap.inong.common.client;

import org.samtuap.inong.config.FeignConfig;
import org.samtuap.inong.domain.chat.dto.MemberDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Member-service", configuration = FeignConfig.class)
public interface MemberFeign {

    @GetMapping(value = "/member/{id}")
    MemberDetailResponse getMemberById(@PathVariable("id") Long id);
}

