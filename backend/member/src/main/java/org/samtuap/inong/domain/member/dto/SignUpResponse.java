package org.samtuap.inong.domain.member.dto;

import lombok.Builder;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.MemberRole;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;

@Builder
public record SignUpResponse(Long memberId,
                             String name,
                             String email,
                             String socialId,
                             SocialType socialType,
                             String role,
                             String accessToken,
                             String refreshToken){
    public static SignUpResponse fromEntity(Member member, JwtToken jwtToken) {
        return SignUpResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .socialId(member.getSocialId())
                .socialType(member.getSocialType())
                .role(MemberRole.MEMBER.toString())
                .accessToken(jwtToken.accessToken())
                .refreshToken(jwtToken.refreshToken())
                .build();
    }
}
