package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberSubscriptionListResponse(Long packageId,
                                             String packageName,
                                             String imageUrl,
                                             String farmId,
                                             String farmName
                                             ){

}
