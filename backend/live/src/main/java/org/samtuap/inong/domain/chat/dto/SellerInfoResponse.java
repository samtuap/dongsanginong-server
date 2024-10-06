package org.samtuap.inong.domain.chat.dto;

public record SellerInfoResponse(
        Long sellerId,
        String name,
        String email
) {
}