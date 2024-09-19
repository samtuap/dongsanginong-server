package org.samtuap.inong.domain.member.oauth.google.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.member.dto.MemberInfoServiceResponse;
import org.samtuap.inong.domain.member.oauth.google.config.GoogleOAuthConfig;
import org.samtuap.inong.domain.member.oauth.google.dto.GoogleGetMemberInfoServiceResponse;
import org.samtuap.inong.domain.member.oauth.service.OAuthService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.samtuap.inong.domain.member.entity.SocialType.*;
import static org.samtuap.inong.domain.member.oauth.google.config.GoogleOAuthConfig.*;

@RequiredArgsConstructor
@Service
public class GoogleService implements OAuthService {

    private final GoogleOAuthConfig googleOAuthConfig;

    public MemberInfoServiceResponse getMemberInfo(final String accessToken) {
        GoogleGetMemberInfoServiceResponse response = null;
        try {
            RestClient restClient = RestClient.create();
            response = restClient.get()
                    .uri(GOOGLE_USER_INFO_URI)
                    .header(AUTHORIZATION, "Bearer " + accessToken)
                    .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (googleRequest, googleResponse) -> {
                                throw new RuntimeException("FAIL_TO_AUTH");
                            })
                    .body(GoogleGetMemberInfoServiceResponse.class);
        } catch(Exception e) {
            throw new RuntimeException("FAIL_TO_AUTH");
        }

        assert response != null;
        return new MemberInfoServiceResponse(response.id(), GOOGLE, response.email());
    }

}
