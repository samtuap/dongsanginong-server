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

    // 각 liveId별로 참여자 수 관리
    private ConcurrentHashMap<String, Integer> liveParticipantsMap = new ConcurrentHashMap<>();

    // 새로운 사용자가 웹 소켓을 연결할 때 실행됨
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // liveId를 WebSocket 연결 시 클라이언트가 전송하는 헤더에서 가져오기
        String liveId = headerAccessor.getFirstNativeHeader("sessionId");
        if (liveId != null) {
            liveParticipantsMap.merge(liveId, 1, Integer::sum); // 해당 liveId의 참여자 수 증가
            LOGGER.info("새로운 WebSocket 연결: liveId = {}, 현재 참여자 수 = {}", liveId, liveParticipantsMap.get(liveId));

            // 실시간 참여자 수를 해당 채팅방으로 전송
            messagingTemplate.convertAndSend("/topic/live/" + liveId + "/participants", liveParticipantsMap.get(liveId));
            LOGGER.info("참여자 수 전송: liveId = {}, 현재 참여자 수 = {}", liveId, liveParticipantsMap.get(liveId));
        }
    }

    // 사용자가 웹 소켓 연결을 끊으면 실행됨
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // liveId를 WebSocket 연결 해제 시 클라이언트가 전송하는 헤더에서 가져오기
        String liveId = headerAccessor.getFirstNativeHeader("sessionId");
        LOGGER.info("서버 측 연결된 liveId: {}", liveId);
        if (liveId != null && liveParticipantsMap.containsKey(liveId)) {
            liveParticipantsMap.merge(liveId, -1, Integer::sum); // 해당 liveId의 참여자 수 감소

            // 참여자 수가 음수가 되지 않도록 처리
            int currentParticipants = liveParticipantsMap.get(liveId);
            if (currentParticipants < 0) {
                liveParticipantsMap.put(liveId, 0);
                currentParticipants = 0;
            }

            LOGGER.info("WebSocket 연결 해제: liveId = {}, 현재 참여자 수 = {}", liveId, currentParticipants);

            // 실시간 참여자 수를 해당 채팅방으로 전송
            messagingTemplate.convertAndSend("/topic/live/" + liveId + "/participants", currentParticipants);
        }
    }

    public int getParticipantCount(String sessionId) {
        return liveParticipantsMap.getOrDefault(sessionId, 0);  // 세션에 해당하는 참여자 수 반환
    }
}
