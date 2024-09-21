package org.samtuap.inong.domain.notification.api;


import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.notification.dto.FcmTokenSaveRequest;
import org.samtuap.inong.domain.notification.service.FcmService;
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
}
