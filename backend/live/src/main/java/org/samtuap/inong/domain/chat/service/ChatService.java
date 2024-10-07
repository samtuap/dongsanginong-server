package org.samtuap.inong.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.FarmFeign;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.samtuap.inong.domain.chat.dto.MemberDetailResponse;
import org.samtuap.inong.domain.chat.kafka.KafkaConstants;
import org.samtuap.inong.domain.live.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.live.entity.Live;
import org.samtuap.inong.domain.live.repository.LiveRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;
    private final MemberFeign memberFeign;
    private final FarmFeign farmFeign;
    private final LiveRepository liveRepository;

    public void processAndSendMessage(String liveId, ChatMessageRequest messageRequest) {
        Long memberId = messageRequest.memberId();
        Long sellerId = messageRequest.sellerId();

        // 메시지 전송을 위한 기본 로직
        String senderName = messageRequest.name();
        boolean isOwner = false;

        // 개설자인지 여부 확인
        try {
            Live live = liveRepository.findBySessionId(liveId)
                    .orElseThrow(() -> new IllegalArgumentException("Live not found for id: " + liveId));
            if (live.getOwnerId().equals(sellerId)) {
                isOwner = true;
            }
        } catch (Exception e) {
            log.error("Error fetching live info for liveId: {}", liveId, e);
        }

        log.info("Sending message with name: {}, isOwner: {}", senderName, isOwner);

        // 멤버 ID가 있는 경우 멤버 이름 설정
        if (memberId != null) {
            log.info("Fetching member info for memberId: {}", memberId);
            try {
                MemberDetailResponse memberInfo = memberFeign.getMemberById(memberId);
                if (memberInfo != null) {
                    senderName = memberInfo.name();  // 멤버 이름 사용
                    log.info("Member name retrieved: {}", senderName);
                } else {
                    log.warn("Member not found for memberId: {}", memberId);
                }
            } catch (Exception e) {
                log.error("Error fetching member info for memberId: {}", memberId, e);
            }
        }

        // 판매자일 경우 농장 이름 설정 (isSeller 값은 수정하지 않음)
        if (sellerId != null) {
            log.info("Fetching farm info for sellerId: {}", sellerId);
            try {
                FarmDetailGetResponse farmInfo = farmFeign.getFarmInfoWithSellerId(sellerId);
                if (farmInfo != null) {
                    senderName = farmInfo.farmName();  // 판매자 이름 대신 농장 이름 사용
                    log.info("Farm name retrieved: {}", senderName);
                } else {
                    log.warn("Farm not found for sellerId: {}", sellerId);
                }
            } catch (Exception e) {
                log.error("Error fetching farm info for sellerId: {}", sellerId, e);
            }
        }

        // 메시지 전송
        ChatMessageRequest updatedRequest = ChatMessageRequest.builder()
                .memberId(memberId)
                .sellerId(sellerId)
                .liveId(liveId)
                .name(senderName)  // 이름 설정 (멤버 이름 또는 농장 이름)
                .content(messageRequest.content())
                .type(messageRequest.type())
                .isOwner(isOwner)  // 개설자 여부 설정
                .build();

        log.info("Sending message to Kafka topic: {}, message: {}", KafkaConstants.KAFKA_TOPIC, updatedRequest);
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, updatedRequest);
    }
}
