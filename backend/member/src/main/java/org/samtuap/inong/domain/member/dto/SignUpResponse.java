package org.samtuap.inong.domain.member.dto;

import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;

public record SignUpResponse(Long memberId,
                             String name,
                             String email,
                             String socialId,
                             SocialType socialType,
                             String accessToken,
                             String refreshToken){
    public static SignUpResponse fromEntity(Member member, JwtToken jwtToken) {
        return new SignUpResponse(member.getId(),
                member.getName(),
                member.getEmail(),
                member.getSocialId(),
                member.getSocialType(),
                jwtToken.accessToken(),
                jwtToken.refreshToken());
    }
}
