package org.samtuap.inong.domain.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

    // 새로운 사용자가 웹 소켓을 연결할 때 실행됨
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        LOGGER.info("새로운 WebSocket 연결");
    }

    // 사용자가 웹 소켓 연결을 끊으면 실행됨
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        LOGGER.info("WebSocket 연결 해제됨: " + sessionId);
    }
}
