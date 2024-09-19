package org.samtuap.inong.domain.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.MemberExceptionType;
import org.samtuap.inong.domain.member.dto.MemberInfoServiceResponse;
import org.samtuap.inong.domain.member.dto.SignInResponse;
import org.samtuap.inong.domain.member.dto.SignUpRequest;
import org.samtuap.inong.domain.member.dto.SignUpResponse;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;
import org.samtuap.inong.domain.member.jwt.service.JwtService;
import org.samtuap.inong.domain.member.oauth.google.service.GoogleService;
import org.samtuap.inong.domain.member.oauth.kakao.service.KakaoService;
import org.samtuap.inong.domain.member.dto.MemberDetailResponse;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.MemberExceptionType.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final JwtService jwtService;

    @Transactional
    public SignInResponse signIn(final String socialAccessToken, final SocialType socialType) {
        MemberInfoServiceResponse signedMember = getMemberInfo(socialType, socialAccessToken);
        // DB 에서 회원 찾기
        Optional<Member> optionalMember = memberRepository.findBySocialIdAndSocialType(signedMember.socialId(), socialType);
        Member member = optionalMember.orElseThrow(()->new BaseCustomException(MemberExceptionType.NEED_TO_REGISTER));
        JwtToken jwtToken = jwtService.issueToken(member.getId());
        return SignInResponse.fromEntity(member, jwtToken);
    }

    public SignUpResponse signUp(String socialAccessToken, SignUpRequest signUpRequest) {

        // 1. 회원 정보 가져오기
        MemberInfoServiceResponse memberInfo = getMemberInfo(signUpRequest.socialType(), socialAccessToken);

        // 2. 이미 있는 회원인지 확인
        Optional<Member> existingMember = memberRepository.findBySocialIdAndSocialType(memberInfo.socialId(), memberInfo.socialType());
        if (existingMember.isPresent()) {
            throw new BaseCustomException(MemberExceptionType.NOT_A_NEW_MEMBER);
        }
        // 3. DB에 회원 저장
        Member member = signUpRequest.toEntity(memberInfo.socialId(), memberInfo.email());
        memberRepository.save(member);

        // 4. 토큰 발급
        JwtToken jwtToken = jwtService.issueToken(member.getId());

        // 5. 응답 반환
        return SignUpResponse.fromEntity(member, jwtToken);
    }

    private MemberInfoServiceResponse getMemberInfo(SocialType socialType, String socialAccessToken) {
        return switch (socialType) {
            case KAKAO -> kakaoService.getMemberInfo(socialAccessToken);
            case GOOGLE -> googleService.getMemberInfo(socialAccessToken);
            default -> throw new BaseCustomException(MemberExceptionType.INVALID_SOCIAL_TYPE);
        };
    }

    /**
     * id로 회원 찾아오기
     */
    public MemberDetailResponse findMember(Long id) {

        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BaseCustomException(MEMBER_NOT_FOUND)
        );
        return MemberDetailResponse.from(member);
    }
}
