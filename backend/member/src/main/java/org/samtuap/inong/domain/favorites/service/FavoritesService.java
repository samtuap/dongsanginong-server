package org.samtuap.inong.domain.favorites.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.favorites.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.favorites.dto.FollowersGetResponse;
import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.samtuap.inong.domain.favorites.repository.FavoritesRepository;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final MemberRepository memberRepository;
    private final ProductFeign productFeign;

    public FollowersGetResponse getFollowers(Long farmId) {
        List<Long> followers = favoritesRepository.findByFarmId(farmId).stream()
                .map(favorite -> favorite.getMember().getId()).toList();
        return new FollowersGetResponse(followers);
    }

    /**
     * 즐겨찾기 한 농장 중 라이브 중인 목록 출력
     */
    public List<FavoritesLiveListResponse> favoritesFarmLiveList(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        // 회원이 즐겨찾기 한 농장 목록
        List<Favorites> favoritesList = favoritesRepository.findByMember(member);

        // 즐겨찾기 목록 중 farmId만 추출해서 list 생성 => feign 보내기 위해
        List<Long> favoriteFarmList = favoritesList.stream()
                .map(Favorites::getFarmId)
                .toList();
        return productFeign.getFavoritesFarmLiveList(favoriteFarmList);
    }
}
