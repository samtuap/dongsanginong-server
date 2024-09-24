package org.samtuap.inong.domain.seller.dto;

import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;

public record SellerSignUpResponse(Long sellerId,
                             String name,
                             String email,
                             String accessToken,
                             String refreshToken){

    public static SellerSignUpResponse fromEntity(Seller seller, JwtToken jwtToken) {
        return new SellerSignUpResponse(seller.getId(),
                seller.getName(),
                seller.getEmail(),
                jwtToken.accessToken(),
                jwtToken.refreshToken());
    }
}
