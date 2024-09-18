package org.samtuap.inong.domain.member.oauth.service;

import org.samtuap.inong.domain.member.dto.MemberInfoServiceResponse;

public interface OAuthService {
    MemberInfoServiceResponse getMemberInfo(final String accessToken);
}
