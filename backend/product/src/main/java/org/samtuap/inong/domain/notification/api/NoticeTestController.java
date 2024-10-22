package org.samtuap.inong.domain.notification.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.notification.service.NoticeTestService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NoticeTestController {
    private final NoticeTestService noticeTestService;
    
    // 동기 처리
    @PostMapping("/notification/member/{memberId}/sync")
    public void noticeSync(@PathVariable("memberId") Long memberId) {
        noticeTestService.noticeSync(memberId);
    }

    // 비동기 처리
    @PostMapping("/notification/member/{memberId}/async")
    public void noticeAsync(@PathVariable("memberId") Long memberId) {
        noticeTestService.noticeAsync(memberId);
    }

}
