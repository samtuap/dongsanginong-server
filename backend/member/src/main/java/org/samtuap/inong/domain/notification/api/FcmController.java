package org.samtuap.inong.domain.notification.api;


import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.notification.dto.FcmTokenSaveRequest;
import org.samtuap.inong.domain.notification.dto.NotificationIssueRequest;
import org.samtuap.inong.domain.notification.service.FcmService;
import org.springframework.data.redis.connection.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/fcm")
@RestController
public class FcmController {
    private final FcmService fcmService;
    @PostMapping("/token")
    public ResponseEntity<Void> saveFcmToken(@RequestHeader Long myId,
                                             @RequestBody @Valid FcmTokenSaveRequest fcmTokenSaveRequest) {
        fcmService.saveFcmToken(myId, fcmTokenSaveRequest);
        return ResponseEntity.ok(null);
    }


    // [feign 요청 용] 원하는 내용으로 알림을 보낼 수 있는 API입니다.
    @PostMapping("/notice/issue")
    public ResponseEntity<Void> issueNotice(@RequestHeader Long myId,
                                            @RequestBody @Valid NotificationIssueRequest notiRequest) {
        fcmService.issueNotice(notiRequest);
        return ResponseEntity.ok(null);
    }
}
