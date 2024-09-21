package org.samtuap.inong.domain.member.dto;

import lombok.Builder;
import org.samtuap.inong.domain.member.entity.Member;

@Builder
public record MemberUpdateInfoRequest(String phone, String address, String addressDetail, String zipcode) {

    public static MemberUpdateInfoRequest newInfo(Member member){
        return MemberUpdateInfoRequest.builder()
                .phone(member.getPhone())
                .address(member.getAddress())
                .addressDetail(member.getAddressDetail())
                .zipcode(member.getZipcode())
                .build();
    }
}
