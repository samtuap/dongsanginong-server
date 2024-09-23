package org.samtuap.inong.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberFavoriteFarmResponse(String farmName,
                                         String profileImageUrl) {
}
