package org.samtuap.inong.domain.member.dto;

import org.samtuap.inong.domain.member.entity.SocialType;

public record SignInRequest(SocialType socialType) {
}
