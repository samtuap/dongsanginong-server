package org.samtuap.inong.domain.member.oauth.kakao.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KakaoOAuthConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String KAKAO_URI = "https://kapi.kakao.com/v2/user/me"; // 사용자 정보 가져오기 url

    // client_id = rest_api_key
    @Value("${kakao.login.api_key}")
    private String kakaoLoginApiKey;

    // redirect_uri
    @Value("${kakao.login.redirect_uri}")
    private String redirectUri;

    // oauth/authorize
    @Value("${kakao.login.uri.code}")
    private String codeRequestUri;

    // https://kapi.kakao.com
    @Value("${kakao.login.uri.base}")
    private String kakaoAuthBaseUri;

    // oauth/token
    @Value("${kakao.login.uri.token}")
    private String tokenRequestUri;

    // https://kauth.kakao.com
    @Value("${kakao.api.uri.base}")
    private String kakaoApiBaseUri;

    // v2/user/me
    @Value("${kakao.api.uri.user}")
    private String kakaoApiUserInfoRequestUri;

}
