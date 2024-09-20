package org.samtuap.inong.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.MemberExceptionType;
import org.samtuap.inong.domain.member.dto.*;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.MemberRole;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;
import org.samtuap.inong.domain.member.jwt.service.JwtService;
import org.samtuap.inong.domain.member.oauth.google.service.GoogleService;
import org.samtuap.inong.domain.member.oauth.kakao.service.KakaoService;
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
        JwtToken jwtToken = jwtService.issueToken(member.getId(), MemberRole.MEMBER.toString());
        return SignInResponse.fromEntity(member, jwtToken);
    }

    @Transactional
    public SignUpResponse signUp(String socialAccessToken, SignUpRequest signUpRequest) {
        MemberInfoServiceResponse memberInfo = getMemberInfo(signUpRequest.socialType(), socialAccessToken);
        Optional<Member> existingMember = memberRepository.findBySocialIdAndSocialType(memberInfo.socialId(), memberInfo.socialType());
        if (existingMember.isPresent()) {
            throw new BaseCustomException(MemberExceptionType.NOT_A_NEW_MEMBER);
        }
        Member member = signUpRequest.toEntity(memberInfo.socialId(), memberInfo.email());
        memberRepository.save(member);
        JwtToken jwtToken = jwtService.issueToken(member.getId(), MemberRole.MEMBER.toString());

        return SignUpResponse.fromEntity(member, jwtToken);
    }

    private MemberInfoServiceResponse getMemberInfo(SocialType socialType, String socialAccessToken) {
        return switch (socialType) {
            case KAKAO -> kakaoService.getMemberInfo(socialAccessToken);
            case GOOGLE -> googleService.getMemberInfo(socialAccessToken);
            default -> throw new BaseCustomException(MemberExceptionType.INVALID_SOCIAL_TYPE);
        };
    }

    @Transactional
    public void signOut(final Long memberId) {
        jwtService.deleteRefreshToken(memberId);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        memberRepository.deleteById(member.getId());
        jwtService.deleteRefreshToken(member.getId());
    }

    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);

        return MemberInfoResponse.fromEntity(member);
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
