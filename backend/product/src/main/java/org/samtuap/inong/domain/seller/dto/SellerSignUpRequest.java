package org.samtuap.inong.domain.seller.dto;


import org.samtuap.inong.domain.seller.entity.Seller;

public record SellerSignUpRequest(
    String name,
    String email,
    String password,
    String businessNumber,
    String businessName,
    String zipcode,
    String address,
    String addressDetail
) {
    public static Seller toEntity(SellerSignUpRequest seller, String encodedPassword) {
        return Seller.builder()
            .name(seller.name())
            .email(seller.email())
            .password(encodedPassword)
            .businessNumber(seller.businessNumber())
            .businessName(seller.businessName())
            .zipcode(seller.zipcode())
            .address(seller.address())
            .addressDetail(seller.addressDetail())
            .build();
    }
}
