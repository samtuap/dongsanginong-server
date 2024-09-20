package org.samtuap.inong.domain.member.oauth.google.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class GoogleOAuthConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String GOOGLE_USER_INFO_URI = "https://www.googleapis.com/userinfo/v2/me";

    @Value("${google.login.client_id}")
    private String googleLoginApiKey;

    @Value("${google.login.code_uri}")
    private String codeRequestUri;

    @Value("${google.login.token_uri}")
    private String tokenRequestUri;

    @Value("${google.login.client_secret}")
    private String clientSecret;

    @Value("${google.login.redirect_uri}")
    private String redirectUri;

    @Value("${google.login.code_redirect_uri}")
    private String codeRedirectUri;


}
