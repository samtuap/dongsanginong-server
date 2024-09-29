package org.samtuap.inong.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.samtuap.inong.domain.chat.dto.MemberDetailResponse;
import org.samtuap.inong.domain.chat.kafka.KafkaConstants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static org.samtuap.inong.common.exceptionType.ChatExceptionType.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;
    private final MemberFeign memberFeign;

    public void processAndSendMessage(Long liveId, ChatMessageRequest messageRequest) {
        Long memberId = messageRequest.memberId();
        if (memberId == null) {
            log.error("memberId is missing in the payload");
            throw new BaseCustomException(MEMBER_NOT_FOUND);
        }
        MemberDetailResponse memberInfo = memberFeign.getMemberById(memberId);
        String memberName = memberInfo.name();

        ChatMessageRequest updatedRequest = ChatMessageRequest.builder()
                .memberId(memberId)
                .liveId(liveId)
                .name(memberName)
                .content(messageRequest.content())
                .type(messageRequest.type())
                .build();
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, updatedRequest);
    }
}

