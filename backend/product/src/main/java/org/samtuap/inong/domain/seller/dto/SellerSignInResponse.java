package org.samtuap.inong.domain.seller.dto;

import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.entity.Seller;

public record SellerSignInResponse(Long sellerId,
                                   String name,
                                   String email,
                                   String accessToken,
                                   String refreshToken){
    public static SellerSignInResponse fromEntity(Seller seller, JwtToken jwtToken) {
        return new SellerSignInResponse(seller.getId(),
                seller.getName(),
                seller.getEmail(),
                jwtToken.accessToken(),
                jwtToken.refreshToken());
    }
}
