package org.samtuap.inong.domain.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.samtuap.inong.domain.chat.dto.CouponMessage;
import org.samtuap.inong.domain.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{sessionId}/sendMessage")
    public void sendMessage(@DestinationVariable ("sessionId") String sessionId,
                            @Payload ChatMessageRequest messageRequest) {
        chatService.processAndSendMessage(sessionId, messageRequest);
    }

    @PostMapping("/{sessionId}/kick/{userId}")
    public ResponseEntity<String> kickUser(@PathVariable("sessionId") String sessionId,
                                           @PathVariable("userId") Long userId,
                                           @RequestHeader("sellerId") Long requestSellerId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("ID : null");
        }
        log.info("강퇴 요청: sessionId = {}, userId = {}, requestSellerId = {}", sessionId, userId, requestSellerId);
        chatService.kickUser(sessionId, userId, requestSellerId);
        return ResponseEntity.ok("사용자가 강퇴되었습니다.");
    }

    @GetMapping("/{sessionId}/isKicked/{memberId}")
    public ResponseEntity<Boolean> isUserKicked(@PathVariable("sessionId") String sessionId,
                                                @PathVariable("memberId") Long memberId) {
        boolean isKicked = chatService.isUserKicked(sessionId, memberId);
        return ResponseEntity.ok(isKicked);
    }

    @MessageMapping("/chat/{sessionId}/sendCoupon")
    public void sendCoupon(@DestinationVariable ("sessionId") String sessionId, CouponMessage couponMessage) {
        // 쿠폰 메시지를 모든 시청자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/live/" + sessionId, couponMessage);
    }
}
