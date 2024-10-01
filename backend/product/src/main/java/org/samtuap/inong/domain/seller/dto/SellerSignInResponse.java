package org.samtuap.inong.domain.seller.dto;

import lombok.Builder;
import org.samtuap.inong.domain.seller.entity.SellerRole;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.entity.Seller;

@Builder
public record SellerSignInResponse(Long sellerId,
                                   String name,
                                   String email,
                                   String accessToken,
                                   String refreshToken,
                                   String role){
    public static SellerSignInResponse fromEntity(Seller seller, JwtToken jwtToken) {
        return SellerSignInResponse.builder()
                .sellerId(seller.getId())
                .name(seller.getName())
                .email(seller.getEmail())
                .accessToken(jwtToken.accessToken())
                .refreshToken(jwtToken.refreshToken())
                .role(SellerRole.SELLER.toString())
                .build();
    }
}
