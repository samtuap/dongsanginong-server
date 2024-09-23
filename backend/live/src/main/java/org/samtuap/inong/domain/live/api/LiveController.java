package org.samtuap.inong.domain.live.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.live.dto.ActiveLiveListGetResponse;
import org.samtuap.inong.domain.live.service.LiveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/live")
@RestController
public class LiveController {

    private final LiveService liveService;

    @GetMapping("/active")
    public ResponseEntity<List<ActiveLiveListGetResponse>> getActiveLiveList() {
        List<ActiveLiveListGetResponse> response = liveService.getActiveLiveList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
