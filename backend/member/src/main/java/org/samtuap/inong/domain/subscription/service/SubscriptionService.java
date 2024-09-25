package org.samtuap.inong.domain.subscription.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.SubscriptionExceptionType;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.notification.dto.KafkaNotificationRequest;
import org.samtuap.inong.domain.subscription.dto.KafkaSubscribeProductRequest;
import org.samtuap.inong.domain.subscription.dto.PackageProductListGetRequest;
import org.samtuap.inong.domain.subscription.dto.SubscriptionListGetResponse;
import org.samtuap.inong.domain.subscription.entity.Subscription;
import org.samtuap.inong.domain.subscription.repository.SubscriptionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.FCM_SEND_FAIL;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.INVALID_FCM_REQUEST;
import static org.samtuap.inong.common.exceptionType.SubscriptionExceptionType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void registerBillingKey(Long memberId, String billingKey) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        member.updateBillingKey(billingKey);
    }


    public SubscriptionListGetResponse getSubscriptionToPay() {
        List<Subscription> subscriptions = subscriptionRepository.findAllByPayDate(LocalDate.now());

        List<Long> packageProductIds = subscriptions.stream()
                .map(Subscription::getId)
                .toList();

        List<SubscriptionListGetResponse.SubscriptionGetResponse> list = subscriptions.stream()
                .map(SubscriptionListGetResponse.SubscriptionGetResponse::fromEntity)
                .toList();

        // 다음 결제일 변경
        updatePayDates(subscriptions);

        return new SubscriptionListGetResponse(list);
    }

    private void updatePayDates(List<Subscription> subscriptions) {
        subscriptions.forEach(s -> s.updatePayDate(s.getPayDate().plusDays(28)));
    }

    @Transactional
    public void cancelSubscription(Long memberId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findByIdOrThrow(subscriptionId);

        if(!subscription.getMember().getId().equals(memberId)) {
            throw new BaseCustomException(FORBIDDEN);
        }

        subscriptionRepository.delete(subscription);
    }

    //== Kafka를 통한 정기 구독 비동기 처리 ==//
    @KafkaListener(topics = "subscription-topic", groupId = "order-group",/*order group으로 부터 메시지가 들어오면*/ containerFactory = "kafkaListenerContainerFactory")
    public void consumeIssueNotification(String message /*listen 하면 스트링 형태로 메시지가 들어온다*/) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KafkaSubscribeProductRequest subscribeRequest = objectMapper.readValue(message, KafkaSubscribeProductRequest.class);
            subscribePackageProduct(subscribeRequest);
        } catch (JsonProcessingException e) {
            throw new BaseCustomException(INVALID_FCM_REQUEST);
        } catch(Exception e) {
            throw new BaseCustomException(FCM_SEND_FAIL);
        }
    }

    private void subscribePackageProduct(KafkaSubscribeProductRequest subscribeRequest) {
        Member member = memberRepository.findByIdOrThrow(subscribeRequest.memberId());
        Subscription subscription = Subscription.builder()
                .packageId(subscribeRequest.productId())
                .member(member)
                .payDate(LocalDate.now().plusDays(28))
                .build();
        subscriptionRepository.save(subscription);
    }
}
