package org.samtuap.inong.domain.farmNotice.dto;

// product 모듈 내에서 > FeignClient를 통해 Member에서 가져온 회원 정보를 담은 dto
public record MemberDetailResponse(
        Long id,
        String name,
        String email) {
}