package org.samtuap.inong.domain.order.dto;

// feign 조회용
public record MemberAllInfoResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String addressDetail,
        String zipcode,
        String socialType,
        String socialId,
        String fcmToken,
        String billingKey) {
}