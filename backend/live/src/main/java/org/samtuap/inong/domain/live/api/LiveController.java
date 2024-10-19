package org.samtuap.inong.domain.live.api;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.samtuap.inong.domain.live.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.live.service.LiveService;
import org.springframework.data.domain.PageImpl;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<Integer> getParticipantCount(@PathVariable("sessionId") String sessionId) {
        int count = liveService.getParticipantCount(sessionId);
        return ResponseEntity.ok(count);
    }

    /**
     * main page => 라이브 목록 시청자 순 best5
     */
    @GetMapping("/active/best")
    public ResponseEntity<Page<ActiveLiveListGetResponse>> getBestLive(@PageableDefault(size = 5) Pageable pageable) {
        Page<ActiveLiveListGetResponse> response = liveService.getActiveLiveList(pageable);

        List<ActiveLiveListGetResponse> sortedList = response.stream()
                .sorted(Comparator.comparing(ActiveLiveListGetResponse::participantCount).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        Page<ActiveLiveListGetResponse> sortedPage = new PageImpl<>(sortedList.subList(start, end), pageable, sortedList.size());

        return new ResponseEntity<>(sortedPage, HttpStatus.OK);
    }
}
