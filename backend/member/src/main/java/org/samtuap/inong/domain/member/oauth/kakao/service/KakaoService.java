package org.samtuap.inong.domain.member.oauth.kakao.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.member.dto.MemberInfoServiceResponse;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.samtuap.inong.domain.member.oauth.kakao.config.KakaoOAuthConfig;
import org.samtuap.inong.domain.member.oauth.kakao.dto.KakaoGetMemberInfoServiceResponse;
import org.samtuap.inong.domain.member.oauth.service.OAuthService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class KakaoService implements OAuthService {

    private final KakaoOAuthConfig kakaoOAuthConfig;

    // 카카오 API 에 로그인 요청을 보내고 회원 정보를 가져오기
    public MemberInfoServiceResponse getMemberInfo(final String accessToken){
        KakaoGetMemberInfoServiceResponse response = null;
        RestClient restClient = RestClient.create();
        response = restClient.get()
                .uri(KakaoOAuthConfig.KAKAO_URI)
                .header(KakaoOAuthConfig.AUTHORIZATION, "Bearer " + accessToken)
                .header("Content-type","application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,(kakaoRequest, kakaoResponse) -> {
                    throw new RuntimeException("Fail to Auth");
                })
                .body(KakaoGetMemberInfoServiceResponse.class);
        if (response == null) {
            throw new RuntimeException("Failed to retrieve user information");
        }
        return new MemberInfoServiceResponse(response.id(), SocialType.KAKAO, response.kakao_account().email());
    }

}
