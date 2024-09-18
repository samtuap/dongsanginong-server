package org.samtuap.inong.domain.member.oauth.kakao.dto;

import org.samtuap.inong.domain.member.entity.SocialType;

public record KakaoAccountServiceResponse(String socialId, SocialType socialType, String email) {
}
