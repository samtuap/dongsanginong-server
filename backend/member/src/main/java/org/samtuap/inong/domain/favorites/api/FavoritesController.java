package org.samtuap.inong.domain.favorites.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.favorites.dto.FavoriteGetResponse;
import org.samtuap.inong.domain.favorites.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.favorites.dto.FollowersGetResponse;
import org.samtuap.inong.domain.favorites.service.FavoritesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/favorites")
@RequiredArgsConstructor
@RestController
public class FavoritesController {

    private final FavoritesService favoritesService;

    // [feign 요청 용] 해당 농장을 즐겨찾기한 모든 사용자 아이디 리턴
    @GetMapping("/farm/{farmId}/followers")
    public ResponseEntity<FollowersGetResponse> getFollowers(@PathVariable("farmId") Long farmId) {
        return new ResponseEntity<>(favoritesService.getFollowers(farmId), HttpStatus.OK);
    }

    /**
     * 즐겨찾기 한 농장 중 라이브 중인 목록 출력
     */
    @GetMapping("/farm/live/list")
    public ResponseEntity<List<FavoritesLiveListResponse>> favoritesFarmLiveList(@RequestHeader("myId") Long memberId) {
        return new ResponseEntity<>(favoritesService.favoritesFarmLiveList(memberId), HttpStatus.OK);
    }

    @PostMapping("/farm/{farmId}")
    public ResponseEntity<Void> clickLike(@RequestHeader("myId") Long memberId,
                                          @PathVariable("farmId") Long farmId) {
        favoritesService.clickLike(memberId, farmId);
        return ResponseEntity.ok(null);
    }

    // feign 용
    @GetMapping("/farm/{farmId}")
    public FavoriteGetResponse getFavorite(@RequestParam(value = "memberId", required = false) Long memberId,
                                          @PathVariable("farmId") Long farmId) {
        log.info("line 68: {}", memberId);
        return favoritesService.getFavorite(memberId, farmId);
    }
}
