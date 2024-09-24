package org.samtuap.inong.domain.seller.dto;

import org.samtuap.inong.domain.farm.entity.FarmCategory;

import java.util.List;

public record SellerFarmInfoUpdateRequest(String bannerImageUrl,
                                          String profileImageUrl,
                                          String farmName,
                                          String farmIntro,
                                          List<FarmCategory> category
                                          ) {
}
