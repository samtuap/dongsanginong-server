package org.samtuap.inong.domain.live.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.live.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.live.service.LiveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/live")
@RequiredArgsConstructor
@RestController
public class LiveController {

    private final LiveService liveService;

    /**
     * feign 요청용
     */
    @PostMapping("/farm")
    public List<FavoritesLiveListResponse> getFavoritesFarmLiveList(@RequestBody List<Long> favoriteFarmList) {
        return liveService.getFavoritesFarmLiveList(favoriteFarmList);
    }
}
