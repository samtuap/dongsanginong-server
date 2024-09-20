package org.samtuap.inong.domain.seller.dto;


import org.samtuap.inong.domain.seller.entity.Seller;

public record SellerSignInRequest(
    String email,
    String password
) {
    public static Seller toEntity(SellerSignInRequest seller, String encodedPassword) {
        return Seller.builder()
            .email(seller.email())
            .password(encodedPassword)
            .build();
    }
}
