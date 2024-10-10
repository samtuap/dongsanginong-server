package org.samtuap.inong.domain.chat.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.chat.dto.KickMessage;
import org.samtuap.inong.domain.chat.service.ChatService;
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
@Slf4j
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    // 각 liveId별로 참여자 수 관리
    private ConcurrentHashMap<String, Integer> liveParticipantsMap = new ConcurrentHashMap<>();

    // 새로운 사용자가 웹 소켓을 연결할 때 실행됨
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getFirstNativeHeader("sessionId");
        String memberIdStr = headerAccessor.getFirstNativeHeader("memberId");
        String sellerIdStr = headerAccessor.getFirstNativeHeader("sellerId");
        Long memberId = null;
        Long sellerId = null;

        if (memberIdStr != null && !"null".equals(memberIdStr)) {
            try {
                memberId = Long.valueOf(memberIdStr);
            } catch (NumberFormatException e) {
                log.error("member 형식 X: {}", memberIdStr);
            }
        }

        if (sellerIdStr != null && !"null".equals(sellerIdStr)) {
            try {
                sellerId = Long.valueOf(sellerIdStr);
            } catch (NumberFormatException e) {
                log.error("seller 형식 X: {}", sellerIdStr);
            }
        }

        if (memberId == null && sellerId == null) {
            log.error("member나 seller의 정보가 없음");
            return;
        }

        if (memberId != null) {
            log.info("Member connected: memberId = {}", memberId);
        } else {
            log.info("Seller connected: sellerId = {}", sellerId);
        }

        if (memberId != null && chatService.isUserKicked(sessionId, memberId)) {
            log.info("강퇴된 사용자 접속 시도 차단: sessionId = {}, memberId = {}", sessionId, memberId);
            messagingTemplate.convertAndSend("/topic/kick", new KickMessage(memberId, "You are not allowed to reconnect."));
            return;  // 연결 차단
        }

        // 참여자 수 관리 (얘가 있어야 제대로 나옴)
        headerAccessor.getSessionAttributes().put("sessionId", sessionId);
        liveParticipantsMap.merge(sessionId, 1, Integer::sum);
        log.info("새로운 WebSocket 연결: sessionId = {}, 현재 참여자 수 = {}", sessionId, liveParticipantsMap.get(sessionId));

        messagingTemplate.convertAndSend("/topic/live/" + sessionId + "/participants", liveParticipantsMap.get(sessionId));
    }

    // 사용자가 웹 소켓 연결을 끊으면 실행됨
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // liveId를 WebSocket 연결 해제 시 클라이언트가 전송하는 헤더에서 가져오기
        String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");
        LOGGER.info("서버 측 연결된 sessionId: {}", sessionId);
        if (sessionId != null && liveParticipantsMap.containsKey(sessionId)) {
            liveParticipantsMap.merge(sessionId, -1, Integer::sum); // 해당 liveId의 참여자 수 감소

            // 참여자 수가 음수가 되지 않도록 처리
            int currentParticipants = liveParticipantsMap.get(sessionId);
            if (currentParticipants < 0) {
                liveParticipantsMap.put(sessionId, 0);
                currentParticipants = 0;
            }

            LOGGER.info("WebSocket 연결 해제: sessionId = {}, 현재 참여자 수 = {}", sessionId, currentParticipants);

            // 실시간 참여자 수를 해당 채팅방으로 전송
            messagingTemplate.convertAndSend("/topic/live/" + sessionId + "/participants", currentParticipants);
        }
    }

    public int getParticipantCount(String sessionId) {
        return liveParticipantsMap.getOrDefault(sessionId, 0);  // 세션에 해당하는 참여자 수 반환
    }
}
