package org.samtuap.inong.domain.chat.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.chat.dto.KickMessage;
import org.samtuap.inong.domain.chat.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIVE_PARTICIPANTS_KEY_PREFIX = "live:participants:";

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

        // 강퇴 여부 확인
        if ((memberId != null && chatService.isUserKicked(sessionId, memberId)) ||
                (sellerId != null && chatService.isUserKicked(sessionId, sellerId))) {
            log.info("강퇴된 사용자 접속 시도 차단: sessionId = {}, memberId = {}, sellerId = {}", sessionId, memberId, sellerId);
            messagingTemplate.convertAndSend("/topic/kick", new KickMessage(memberId != null ? memberId : sellerId, "You are not allowed to reconnect."));
            return;  // 연결 차단
        }

        // 참여자 수 관리 (Redis 사용)
        String key = LIVE_PARTICIPANTS_KEY_PREFIX + sessionId;
        redisTemplate.opsForValue().increment(key, 1);

        // 현재 참여자 수 가져오기
        Object countObj = redisTemplate.opsForValue().get(key);
        int currentParticipants = 0;
        if (countObj instanceof Number) {
            currentParticipants = ((Number) countObj).intValue();
        }

        headerAccessor.getSessionAttributes().put("sessionId", sessionId);

        log.info("새로운 WebSocket 연결: sessionId = {}, 현재 참여자 수 = {}", sessionId, currentParticipants);

        // 실시간 참여자 수를 해당 채팅방으로 전송
        messagingTemplate.convertAndSend("/topic/live/" + sessionId + "/participants", currentParticipants);
    }

    // 사용자가 웹 소켓 연결을 끊으면 실행됨
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // liveId를 WebSocket 연결 해제 시 클라이언트가 전송하는 헤더에서 가져오기
        String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");
        LOGGER.info("서버 측 연결된 sessionId: {}", sessionId);
        if (sessionId != null) {
            String key = LIVE_PARTICIPANTS_KEY_PREFIX + sessionId;
            redisTemplate.opsForValue().increment(key, -1);

            // 현재 참여자 수 가져오기
            Object countObj = redisTemplate.opsForValue().get(key);
            int currentParticipants = 0;
            if (countObj instanceof Number) {
                currentParticipants = ((Number) countObj).intValue();
            }

            // 참여자 수가 음수가 되지 않도록 처리
            if (currentParticipants < 0) {
                redisTemplate.opsForValue().set(key, 0);
                currentParticipants = 0;
            }

            LOGGER.info("WebSocket 연결 해제: sessionId = {}, 현재 참여자 수 = {}", sessionId, currentParticipants);

            // 실시간 참여자 수를 해당 채팅방으로 전송
            messagingTemplate.convertAndSend("/topic/live/" + sessionId + "/participants", currentParticipants);
        }
    }

    public int getParticipantCount(String sessionId) {
        String key = LIVE_PARTICIPANTS_KEY_PREFIX + sessionId;
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        return (count != null) ? count : 0;
    }
}
