package org.samtuap.inong.domain.notification.api;

import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.notification.dto.KafkaNotificationRequest;
import org.samtuap.inong.domain.notification.dto.NotificationGetResponse;
import org.samtuap.inong.domain.notification.entity.Notification;
import org.samtuap.inong.domain.notification.service.FcmService;
import org.samtuap.inong.domain.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/notification")
@RequiredArgsConstructor
@RestController
public class NotificationController {
    private final NotificationService notificationService;
    private final FcmService fcmService;

    @GetMapping
    public ResponseEntity<Page<NotificationGetResponse>> getNotification(Pageable pageable, @RequestParam(value = "unread", required = false) boolean unread, @RequestHeader("myId") Long memberId) {
        Page<NotificationGetResponse> notifications = notificationService.getNotifications(pageable, unread, memberId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/read")
    public void readNotifications(@RequestHeader("myId") Long memberId) {
        notificationService.readNotifications(memberId);
    }

    // 성능 테스트 용
    @PostMapping("/member/{memberId}")
    public void sendNotification(@PathVariable Long memberId, KafkaNotificationRequest request) {
        fcmService.issueNoticeSync(memberId, request);
    }

}
