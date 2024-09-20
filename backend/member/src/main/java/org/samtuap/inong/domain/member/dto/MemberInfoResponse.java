package org.samtuap.inong.domain.member.dto;

import lombok.Builder;
import org.samtuap.inong.domain.member.entity.Member;

@Builder
public record MemberInfoResponse(String name, String email, String phone, String address, String addressDetail, String zipcode) {
    public static MemberInfoResponse fromEntity(Member member) {
        return MemberInfoResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .addressDetail(member.getAddressDetail())
                .zipcode(member.getZipcode())
                .build();
    }
}
