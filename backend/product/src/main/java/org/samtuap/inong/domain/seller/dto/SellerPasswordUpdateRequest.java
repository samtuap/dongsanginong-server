package org.samtuap.inong.domain.seller.dto;

public record SellerPasswordUpdateRequest(String oldPassword,
                                          String newPassword) {
}
