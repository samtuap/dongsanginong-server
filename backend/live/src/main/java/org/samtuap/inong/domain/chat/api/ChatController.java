package org.samtuap.inong.domain.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.samtuap.inong.domain.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{sessionId}/sendMessage")
    public void sendMessage(@DestinationVariable ("sessionId") String sessionId,
                            @Payload ChatMessageRequest messageRequest) {
        chatService.processAndSendMessage(sessionId, messageRequest);
    }

    @PostMapping("/{sessionId}/kick/{memberId}")
    public ResponseEntity<String> kickUser(@PathVariable String sessionId,
                                           @PathVariable Long memberId,
                                           @RequestHeader("sellerId") Long sellerId) {
        chatService.kickUser(sessionId, memberId, sellerId);
        return ResponseEntity.ok("사용자가 강퇴되었습니다.");
    }

    @GetMapping("/{sessionId}/isKicked/{memberId}")
    public ResponseEntity<Boolean> isUserKicked(@PathVariable String sessionId,
                                                @PathVariable Long memberId) {
        boolean isKicked = chatService.isUserKicked(sessionId, memberId);
        return ResponseEntity.ok(isKicked);
    }
}
