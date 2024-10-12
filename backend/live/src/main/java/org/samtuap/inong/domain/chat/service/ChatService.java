package org.samtuap.inong.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.FarmFeign;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.samtuap.inong.domain.chat.dto.KickMessage;
import org.samtuap.inong.domain.chat.dto.MemberDetailResponse;
import org.samtuap.inong.domain.chat.kafka.KafkaConstants;
import org.samtuap.inong.domain.live.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.live.entity.Live;
import org.samtuap.inong.domain.live.repository.LiveRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static org.samtuap.inong.common.exceptionType.ChatExceptionType.ID_IS_NULL;
import static org.samtuap.inong.common.exceptionType.ChatExceptionType.IS_NOT_OWNER;
import static org.samtuap.inong.common.exceptionType.LiveExceptionType.LIVE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;
    private final MemberFeign memberFeign;
    private final FarmFeign farmFeign;
    private final LiveRepository liveRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String KICKED_USERS_KEY_PREFIX = "kicked:users:";

    public void processAndSendMessage(String sessionId, ChatMessageRequest messageRequest) {
        Long memberId = messageRequest.memberId();
        Long sellerId = messageRequest.sellerId();

        // 메시지 전송을 위한 기본 로직
        String senderName = messageRequest.name();
        boolean isOwner = false;

        // 사용자가 강퇴된 상태인지 확인
        if ((memberId != null && isUserKicked(sessionId, memberId)) ||
                (sellerId != null && isUserKicked(sessionId, sellerId))) {
            log.info("강퇴된 사용자 메시지 차단: sessionId = {}, memberId = {}, sellerId = {}", sessionId, memberId, sellerId);
            return;
        }

        // 개설자인지 여부 확인
        try {
            Live live = liveRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new BaseCustomException(LIVE_NOT_FOUND));
            if (live.getOwnerId().equals(sellerId)) {
                isOwner = true;
            }
        } catch (Exception e) {
            log.error("세션에 대한 라이브 정보 불러오기 오류: {}", sessionId, e);
        }

        log.info("메시지 전송 name: {}, isOwner: {}", senderName, isOwner);

        // 멤버 ID가 있는 경우 멤버 이름 설정
        if (memberId != null) {
            log.info("멤버 정보 가져오기 memberId: {}", memberId);
            try {
                MemberDetailResponse memberInfo = memberFeign.getMemberById(memberId);
                if (memberInfo != null) {
                    senderName = memberInfo.name();  // 멤버 이름 사용
                } else {
                    log.warn("해당 멤버가 존재하지 않음 memberId: {}", memberId);
                }
            } catch (Exception e) {
                log.error("멤버 정보 가져오는데 오류: {}", memberId, e);
            }
        }

        // 판매자일 경우 농장 이름 설정 (isSeller 값은 수정하지 않음)
        if (sellerId != null) {
            log.info("판매자 농장 정보 가져오기 sellerId: {}", sellerId);
            try {
                FarmDetailGetResponse farmInfo = farmFeign.getFarmInfoWithSellerId(sellerId);
                if (farmInfo != null) {
                    senderName = farmInfo.farmName();  // 판매자 이름 대신 농장 이름 사용
                    log.info("농장 이름: {}", senderName);
                } else {
                    log.warn("해당 판매자의 농장이 없음: {}", sellerId);
                }
            } catch (Exception e) {
                log.error("판매자의 농장 정보 가져오는데 오류: {}", sellerId, e);
            }
        }

        // 메시지 전송
        ChatMessageRequest updatedRequest = ChatMessageRequest.builder()
                .memberId(memberId)
                .sellerId(sellerId)
                .sessionId(sessionId)
                .name(senderName)  // 이름 설정 (멤버 이름 또는 농장 이름)
                .content(messageRequest.content())
                .type(messageRequest.type())
                .isOwner(isOwner)  // 개설자 여부 설정
                .build();

        log.info("Sending message to Kafka topic: {}, message: {}", KafkaConstants.KAFKA_TOPIC, updatedRequest);
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, updatedRequest);
    }

    public void kickUser(String sessionId, Long userId, Long requestSellerId) {
        // 개설자인지 확인 (liveId로 소유자 확인 로직 추가 가능)
        if (!isOwner(sessionId, requestSellerId)) {
            throw new BaseCustomException(IS_NOT_OWNER);
        }

        if (userId == null) {
            throw new BaseCustomException(ID_IS_NULL);
        }

        boolean isMember = isMember(userId);
        // 강퇴된 사용자 추가 (Redis Set 사용)
        String key = KICKED_USERS_KEY_PREFIX + sessionId;
        redisTemplate.opsForSet().add(key, userId);

        String userType = isMember ? "멤버" : "판매자";
        log.info("{} 강퇴됨: sessionId = {}, userId = {}", userType, sessionId, userId);

        messagingTemplate.convertAndSend("/topic/kick/" + userId, new KickMessage(userId, "강퇴되었습니다."));
    }

    private boolean isOwner(String sessionId, Long sellerId) {
        try {
            Live live = liveRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new BaseCustomException(LIVE_NOT_FOUND));

            // ownerId와 sellerId 비교
            return live.getOwnerId().equals(sellerId);
        } catch (Exception e) {
            log.error("owner 확인 중 오류 sessionId: {}, sellerId: {}", sessionId, sellerId, e);
            return false; // 에러가 발생하면 소유자가 아닌 것으로 처리
        }
    }

    // 강퇴된 사용자의 접속을 차단하기 위한 메서드
    public boolean isUserKicked(String sessionId, Long userId) {
        String key = KICKED_USERS_KEY_PREFIX + sessionId;
        Boolean isMemberKicked = redisTemplate.opsForSet().isMember(key, userId);
        return Boolean.TRUE.equals(isMemberKicked);
    }

    private boolean isMember(Long userId) {
        // userId로 멤버 검증 로직 구현
        try {
            memberFeign.getMemberById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
