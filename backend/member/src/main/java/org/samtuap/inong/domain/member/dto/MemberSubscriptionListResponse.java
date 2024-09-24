package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberSubscriptionListResponse(String packageName,
                                             String imageUrl) {

}
