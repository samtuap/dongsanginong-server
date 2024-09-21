package org.samtuap.inong.domain.delivery.dto;

// feignclient 조회용
public record MemberDetailResponse(
        Long id,
        String name,
        String email) {
}
