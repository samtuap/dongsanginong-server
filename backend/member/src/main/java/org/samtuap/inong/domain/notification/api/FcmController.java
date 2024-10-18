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
                                             @RequestBody FcmTokenSaveRequest fcmTokenSaveRequest) {
        fcmService.saveFcmToken(myId, fcmTokenSaveRequest);
        return ResponseEntity.ok(null);
    }


    @PostMapping("/notice/issue")
    public ResponseEntity<Void> issueNotice(@RequestBody @Valid NotificationIssueRequest notiRequest) {
        fcmService.issueNotice(notiRequest);
        return ResponseEntity.ok(null);
    }
}
