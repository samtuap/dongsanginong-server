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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private final ConcurrentHashMap<String, Set<Long>> kickedUsersMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public void processAndSendMessage(String sessionId, ChatMessageRequest messageRequest) {
        Long memberId = messageRequest.memberId();
        Long sellerId = messageRequest.sellerId();

        // 메시지 전송을 위한 기본 로직
        String senderName = messageRequest.name();
        boolean isOwner = false;

        // 사용자가 강퇴된 상태인지 확인
        if (isUserKicked(sessionId, memberId)) {
            log.info("강퇴된 사용자 메시지 차단: sessionId = {}, memberId = {}", sessionId, memberId);
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

    public void kickUser(String sessionId, Long memberId, Long sellerId) {
        // 개설자인지 확인 (liveId로 소유자 확인 로직 추가 가능)
        if (!isOwner(sessionId, sellerId)) {
            throw new BaseCustomException(IS_NOT_OWNER);
        }

        // 강퇴된 사용자 추가
        kickedUsersMap.computeIfAbsent(sessionId, k -> new HashSet<>()).add(memberId);
        log.info("사용자 강퇴됨: sessionId = {}, memberId = {}", sessionId, memberId);

        // 강퇴된 사용자에게 강퇴 메시지 전송
        messagingTemplate.convertAndSend("/topic/kick/" + memberId, new KickMessage(memberId, "강퇴되었습니다."));
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
    public boolean isUserKicked(String sessionId, Long memberId) {
        return kickedUsersMap.containsKey(sessionId) && kickedUsersMap.get(sessionId).contains(memberId);
    }
}
