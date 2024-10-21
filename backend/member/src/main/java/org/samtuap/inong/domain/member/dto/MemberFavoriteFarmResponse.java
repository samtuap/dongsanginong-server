package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberFavoriteFarmResponse(Long farmId,
                                         String farmName,
                                         String profileImageUrl) {
}
