package org.samtuap.inong.domain.chat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = KafkaConstants.KAFKA_TOPIC, groupId = "chat")
    public void listen(@Payload ChatMessageRequest message) {
        log.info("Received message from Kafka: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/live/" + message.liveId(), message);
    }
}
