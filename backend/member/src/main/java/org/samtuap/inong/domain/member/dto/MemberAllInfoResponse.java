package org.samtuap.inong.domain.member.dto;

import lombok.Builder;
import org.samtuap.inong.domain.member.entity.Member;

// feign 조회용
@Builder
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
        String billingKey) {

    public static MemberAllInfoResponse fromEntity(Member member) {
        return MemberAllInfoResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .addressDetail(member.getAddressDetail())
                .zipcode(member.getZipcode())
                .socialType(member.getSocialType().name())
                .socialId(member.getSocialId())
                .billingKey(member.getBillingKey())
                .build();
    }
}