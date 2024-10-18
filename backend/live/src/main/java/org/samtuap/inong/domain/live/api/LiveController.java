package org.samtuap.inong.domain.live.api;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.samtuap.inong.domain.live.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.live.service.LiveService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.samtuap.inong.domain.live.dto.ActiveLiveListGetResponse;

import java.util.List;

@RequestMapping("/live")
@RequiredArgsConstructor
@RestController
public class LiveController {

    private final LiveService liveService;

    @GetMapping("/active")
    public ResponseEntity<Page<ActiveLiveListGetResponse>> getActiveLiveList(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ActiveLiveListGetResponse> response = liveService.getActiveLiveList(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * feign 요청용
     */
    @PostMapping("/farm")
    public List<FavoritesLiveListResponse> getFavoritesFarmLiveList(@RequestBody List<Long> favoriteFarmList) {
        return liveService.getFavoritesFarmLiveList(favoriteFarmList);
    }

    @GetMapping("/{sessionId}/participants")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable String sessionId) {
        int count = liveService.getParticipantCount(sessionId);
        return ResponseEntity.ok(count);
    }
}
