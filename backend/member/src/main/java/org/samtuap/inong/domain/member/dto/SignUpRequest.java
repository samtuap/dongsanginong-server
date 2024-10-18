package org.samtuap.inong.domain.member.dto;

import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.PaymentMethodType;
import org.samtuap.inong.domain.member.entity.SocialType;

import static org.samtuap.inong.domain.member.entity.PaymentMethodType.NONE;

public record SignUpRequest(SocialType socialType,
                            String name,
                            String phone,
                            String address,
                            String addressDetail,
                            String zipcode
) {
    public Member toEntity(String socialId, String email) {
        return Member.builder()
                .socialType(this.socialType)
                .name(this.name)
                .email(email)
                .phone(this.phone)
                .address(this.address)
                .addressDetail(this.addressDetail)
                .zipcode(this.zipcode)
                .socialId(socialId)
                .paymentMethod(NONE)
                .build();
    }
}
