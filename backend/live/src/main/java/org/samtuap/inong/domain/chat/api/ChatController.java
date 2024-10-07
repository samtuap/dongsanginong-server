package org.samtuap.inong.domain.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.samtuap.inong.domain.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{liveId}/sendMessage")
    public void sendMessage(@DestinationVariable ("liveId") String liveId,
                            @Payload ChatMessageRequest messageRequest) {
        chatService.processAndSendMessage(liveId, messageRequest);
    }
}
