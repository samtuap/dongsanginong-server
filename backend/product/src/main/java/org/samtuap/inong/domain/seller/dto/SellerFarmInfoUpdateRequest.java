package org.samtuap.inong.domain.seller.dto;

import java.util.List;

public record SellerFarmInfoUpdateRequest(String bannerImageUrl,
                                          String profileImageUrl,
                                          String farmName,
                                          String farmIntro,
                                          List<Long> categoryId
                                          ) {
}
