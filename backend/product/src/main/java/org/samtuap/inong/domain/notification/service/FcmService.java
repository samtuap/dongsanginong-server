package org.samtuap.inong.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.notification.dto.FcmTokenSaveRequest;
import org.samtuap.inong.domain.notification.dto.KafkaNotificationRequest;
import org.samtuap.inong.domain.notification.dto.NotificationIssueRequest;
import org.samtuap.inong.domain.notification.entity.FcmToken;
import org.samtuap.inong.domain.notification.entity.SellerNotification;
import org.samtuap.inong.domain.notification.repository.FcmTokenRepository;
import org.samtuap.inong.domain.notification.repository.SellerNotificationRepository;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.repository.SellerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.FCM_SEND_FAIL;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.INVALID_FCM_REQUEST;


@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {
    private final SellerRepository sellerRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FarmRepository farmRepository;
    private final SellerNotificationRepository sellerNotificationRepository;

    @Transactional
    public void saveFcmToken(Long memberId, FcmTokenSaveRequest fcmTokenSaveRequest) {
        Seller seller = sellerRepository.findByIdOrThrow(memberId);
        Optional<FcmToken> fcmTokenOpt = fcmTokenRepository.findByToken(fcmTokenSaveRequest.fcmToken());

        if(fcmTokenOpt.isPresent()) { // 이미 저장돼 있는 경우
            return;
        }

        FcmToken fcmToken = FcmToken.builder()
                .token(fcmTokenSaveRequest.fcmToken())
                .seller(seller).build();

        fcmTokenRepository.save(fcmToken);
    }

    public void issueNotice(NotificationIssueRequest notiRequest) {
        List<Long> targets = notiRequest.targets();
        for (Long memberId : targets) {
            issueMessage(memberId, notiRequest.title(), notiRequest.content(), "");
        }
    }

    protected void issueMessage(Long sellerId, String title, String content, String url) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllBySeller(seller);

        for (FcmToken fcmToken : fcmTokens) {
            String token = fcmToken.getToken();

            if(token == null || token.isEmpty()) {
                return;
            }

            SellerNotification noti = SellerNotification.builder()
                    .title(title)
                    .content(content)
                    .seller(seller)
                    .url(url)
                    .isRead(false)
                    .build();

            sellerNotificationRepository.save(noti);

            Message message = Message.builder()
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setTitle(title)
                                    .setBody(content)
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


    }

    //== Kafka를 통한 알림 전송 비동기 처리 ==//
    @Transactional
    @KafkaListener(topics = "seller-notification-topic", groupId = "seller-notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeIssueNotification(String message /*listen 하면 스트링 형태로 메시지가 들어온다*/) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KafkaNotificationRequest notificationRequest = objectMapper.readValue(message, KafkaNotificationRequest.class);
            Farm farm = farmRepository.findByIdOrThrow(notificationRequest.memberId());
            log.info("line 101 {}", notificationRequest);
            this.issueMessage(farm.getSellerId(), notificationRequest.title(), notificationRequest.content(), notificationRequest.url());
        } catch (JsonProcessingException e) {
            throw new BaseCustomException(INVALID_FCM_REQUEST);
        } catch(Exception e) {
            throw new BaseCustomException(FCM_SEND_FAIL);
        }
    }
}
