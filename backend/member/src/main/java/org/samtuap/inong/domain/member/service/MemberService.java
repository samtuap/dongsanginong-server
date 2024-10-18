package org.samtuap.inong.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.MemberExceptionType;
import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.samtuap.inong.domain.favorites.repository.FavoritesRepository;
import org.samtuap.inong.domain.member.dto.*;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.MemberRole;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;
import org.samtuap.inong.domain.member.jwt.service.JwtService;
import org.samtuap.inong.domain.member.oauth.google.service.GoogleService;
import org.samtuap.inong.domain.member.oauth.kakao.service.KakaoService;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.MemberExceptionType.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final FavoritesRepository favoritesRepository;
    private final ProductFeign productFeign;
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

    @Transactional
    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);

        return MemberInfoResponse.fromEntity(member);
    }

    /**
     * feignClient 요청용
     */
    @Transactional
    public MemberDetailResponse findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BaseCustomException(MEMBER_NOT_FOUND)
        );
        return MemberDetailResponse.from(member);
    }

    /**
     * feignClient 요청용
     */
    public List<MemberDetailResponse> findMemberList(List<Long> ids) {
        return memberRepository.findByAllByIds(ids).stream()
                .map(MemberDetailResponse::from)
                .toList();
    }

    public List<MemberDetailResponse> findAllByIdContainDeleted(List<Long> ids) {
        return memberRepository.findAllByIdContainDeleted(ids).stream()
                .map(MemberDetailResponse::from)
                .toList();
    }
    /**
     * feignClient 요청용
     */
    @Transactional
    public MemberAllInfoResponse findAllInfoOfMember(Long id) {
        Member member = memberRepository.findByIdOrThrow(id);
        return MemberAllInfoResponse.fromEntity(member);
    }

    @Transactional
    public MemberUpdateInfoRequest updateMemberInfo(MemberUpdateInfoRequest updateInfo, Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        member.updatePhone(updateInfo.phone());
        member.updateAddress(updateInfo.address(), updateInfo.addressDetail(), updateInfo.zipcode());

        return MemberUpdateInfoRequest.newInfo(member);
    }


    public List<MemberFavoriteFarmResponse> getFavoriteFarm(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        List<Long> farmFavoriteIds = favoritesRepository.findAllByMember(member).stream()
                .map(Favorites::getFarmId)
                .toList();
        List<FarmFavoriteResponse> favoriteFarmResponse = productFeign.getFarmFavoriteList(farmFavoriteIds);
        return favoriteFarmResponse.stream()
                .map(farmFavoriteResponse -> MemberFavoriteFarmResponse.builder()
                        .farmId(farmFavoriteResponse.farmId())
                        .farmName(farmFavoriteResponse.farmName())
                        .profileImageUrl(farmFavoriteResponse.profileImageUrl())
                        .build())
                .toList();
    }



}
