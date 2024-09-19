package org.samtuap.inong.domain.member.dto;

import org.samtuap.inong.domain.member.entity.SocialType;

public record MemberInfoServiceResponse(String socialId, SocialType socialType, String email) {
}
