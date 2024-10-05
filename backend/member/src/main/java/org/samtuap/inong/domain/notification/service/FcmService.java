package org.samtuap.inong.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.thoughtworks.xstream.core.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.NotificationExceptionType;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.notification.dto.FcmTokenSaveRequest;
import org.samtuap.inong.domain.notification.dto.KafkaNotificationRequest;
import org.samtuap.inong.domain.notification.dto.NotificationBody;
import org.samtuap.inong.domain.notification.dto.NotificationIssueRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.FCM_SEND_FAIL;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.INVALID_FCM_REQUEST;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {
    private final MemberRepository memberRepository;
    @Transactional
    public void saveFcmToken(Long memberId, FcmTokenSaveRequest fcmTokenSaveRequest) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        member.updateFcmToken(fcmTokenSaveRequest.fcmToken());

    }

    public void issueNotice(NotificationIssueRequest notiRequest) {
        List<Long> targets = notiRequest.targets();
        for (Long memberId : targets) {
            issueMessage(memberId, notiRequest.title(), notiRequest.content());
        }
    }

    private void issueMessage(Long memberId, String title, String content) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        String token = member.getFcmToken();
        LocalDateTime issuedAt = LocalDateTime.now();

        if(token == null || token.isEmpty()) {
            return;
        }

        NotificationBody notification = NotificationBody.builder()
                .content(content)
                .issuedAt(issuedAt.toString())
                .build();

        ObjectMapper om = new ObjectMapper();
        String notificationBody;
        try {
            notificationBody = om.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new BaseCustomException(INVALID_FCM_REQUEST);
        }

        Message message = Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(notificationBody)
                                .build())
                        .build())
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().sendAsync(message);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new BaseCustomException(FCM_SEND_FAIL);
        }
    }

    //== Kafka를 통한 알림 전송 비동기 처리 ==//
    @KafkaListener(topics = "send-notification-topic", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeIssueNotification(String message /*listen 하면 스트링 형태로 메시지가 들어온다*/) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KafkaNotificationRequest notificationRequest = objectMapper.readValue(message, KafkaNotificationRequest.class);
            this.issueMessage(notificationRequest.memberId(), notificationRequest.title(), notificationRequest.content());
        } catch (JsonProcessingException e) {
            throw new BaseCustomException(INVALID_FCM_REQUEST);
        } catch(Exception e) {
            throw new BaseCustomException(FCM_SEND_FAIL);
        }
    }
}
