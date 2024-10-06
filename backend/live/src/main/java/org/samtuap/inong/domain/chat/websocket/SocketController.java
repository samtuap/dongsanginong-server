package org.samtuap.inong.domain.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

    private final SimpMessagingTemplate messagingTemplate;

    // 각 liveId 별로 참여자를 관리하는 맵
    private ConcurrentHashMap<String, Integer> liveParticipantsMap = new ConcurrentHashMap<>();

    // 새로운 사용자가 웹 소켓을 연결할 때 실행됨
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // liveId를 WebSocket 연결 시 클라이언트가 전송하는 헤더에서 가져와야 함
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        if (sessionId != null) {
            liveParticipantsMap.merge(sessionId, 1, Integer::sum); // 해당 liveId의 참여자 수 증가
            LOGGER.info("새로운 WebSocket 연결: sessionId =" + sessionId  + ", 현재 참여자 수=" + liveParticipantsMap.get(sessionId));

            LOGGER.info("참여자 수 전송 경로: /topic/live/" + sessionId + "/participants, 참여자 수: " + liveParticipantsMap.get(sessionId));
            // 실시간 참여자 수를 해당 채팅방으로 전송
            messagingTemplate.convertAndSend("/topic/live/" + sessionId + "/participants", liveParticipantsMap.get(sessionId));
            LOGGER.info("참여자 수 전송: liveId=" + sessionId + ", 현재 참여자 수=" + liveParticipantsMap.get(sessionId));

        }
    }

    // 사용자가 웹 소켓 연결을 끊으면 실행됨
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // liveId를 WebSocket 연결 해제 시 클라이언트가 전송하는 헤더에서 가져와야 함
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        LOGGER.info("서버 측 연결된 sessionId: " + sessionId);
        if (sessionId != null && liveParticipantsMap.containsKey(sessionId )) {
            liveParticipantsMap.merge(sessionId, -1, Integer::sum); // 해당 liveId의 참여자 수 감소
            LOGGER.info("WebSocket 연결 해제: sessionId=" + sessionId + ", 현재 참여자 수=" + liveParticipantsMap.get(sessionId));

            LOGGER.info("참여자 수 전송 경로: /topic/live/" + sessionId + "/participants, 참여자 수: " + liveParticipantsMap.get(sessionId));
            // 실시간 참여자 수를 해당 채팅방으로 전송
            messagingTemplate.convertAndSend("/topic/live/" + sessionId + "/participants", liveParticipantsMap.get(sessionId));

        }
    }
}
