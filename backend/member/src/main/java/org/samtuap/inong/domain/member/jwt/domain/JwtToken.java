package org.samtuap.inong.domain.member.jwt.domain;

import lombok.Builder;


@Builder
public record JwtToken(String accessToken, String refreshToken) {

}