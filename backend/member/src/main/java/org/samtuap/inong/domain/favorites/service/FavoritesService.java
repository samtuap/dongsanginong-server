package org.samtuap.inong.domain.favorites.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.favorites.dto.FavoriteGetResponse;
import org.samtuap.inong.domain.favorites.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.favorites.dto.FollowersGetResponse;
import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.samtuap.inong.domain.favorites.repository.FavoritesRepository;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final MemberRepository memberRepository;
    private final ProductFeign productFeign;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
        List<Long> farmFavoriteIds = favoritesList.stream()
                .map(Favorites::getFarmId)
                .toList();
        return productFeign.getFavoritesFarmLiveList(farmFavoriteIds);
    }

    @Transactional
    public void clickLike(Long memberId, Long farmId) {
        Member member = memberRepository.findByIdOrThrow(memberId);

        Optional<Favorites> favoriteOpt = favoritesRepository.findByMemberAndFarmId(member, farmId);
        if(favoriteOpt.isPresent()) {
            favoritesRepository.delete(favoriteOpt.get());
            // product 모듈에 feign 요청
            productFeign.decreaseLike(farmId);
            kafkaTemplate.send("update-like", memberId);
        } else {
            Favorites favorite = Favorites.builder()
                    .member(member)
                    .farmId(farmId)
                    .build();
            favoritesRepository.save(favorite);
            productFeign.increaseLike(farmId);
            kafkaTemplate.send("update-like", memberId);
        }
    }

    public FavoriteGetResponse getFavorite(Long memberId, Long farmId) {
        Optional<Favorites> favoriteOpt;
        if(memberId == null) {
            return null;
        } else {
            Member member = memberRepository.findByIdOrThrow(memberId);
            favoriteOpt = favoritesRepository.findByMemberAndFarmId(member, farmId);
        }


        if(favoriteOpt.isEmpty()) {
            return null;
        } else {
            Favorites favorites = favoriteOpt.get();
            return FavoriteGetResponse.builder()
                    .favoriteId(favorites.getId())
                    .farmId(favorites.getFarmId())
                    .memberId(favorites.getMember().getId())
                    .build();
        }
    }
}
