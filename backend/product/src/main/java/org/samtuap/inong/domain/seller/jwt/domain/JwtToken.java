package org.samtuap.inong.domain.seller.jwt.domain;

import lombok.Builder;


@Builder
public record JwtToken(String accessToken, String refreshToken) {

}