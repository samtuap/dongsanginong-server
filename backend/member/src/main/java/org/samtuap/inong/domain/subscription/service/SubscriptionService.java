package org.samtuap.inong.domain.subscription.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.SubscriptionExceptionType;
import org.samtuap.inong.domain.member.dto.*;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.notification.dto.KafkaNotificationRequest;
import org.samtuap.inong.domain.subscription.dto.KafkaOrderRollbackRequest;
import org.samtuap.inong.domain.subscription.dto.KafkaSubscribeProductRequest;
import org.samtuap.inong.domain.subscription.dto.PackageProductListGetRequest;
import org.samtuap.inong.domain.subscription.dto.SubscriptionListGetResponse;
import org.samtuap.inong.domain.subscription.entity.Subscription;
import org.samtuap.inong.domain.subscription.repository.SubscriptionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final ProductFeign productFeign;
    private final KafkaTemplate<String, Object> kafkaTemplate;


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


    public MemberSubscriptionResponse getSubscription(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        Subscription subscription = subscriptionRepository.findByMemberOrThrow(member);
        Long packageProductId = subscription.getPackageId();
        PackageProductResponse packageProduct = productFeign.getPackageProduct(packageProductId);

        return MemberSubscriptionResponse.fromEntity(packageProduct);
    }

    public List<MemberSubscriptionListResponse> getSubscriptionList(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        List<Long> subscriptionIds = subscriptionRepository.findAllByMember(member).stream()
                .map(Subscription::getPackageId)
                .toList();
        List<PackageProductSubsResponse> subscriptionList = productFeign.getProductSubsList(subscriptionIds);
        return subscriptionList.stream()
                .map(subscriptionProductList -> MemberSubscriptionListResponse.builder()
                        .packageId(subscriptionProductList.packageId())
                        .packageName(subscriptionProductList.packageName())
                        .imageUrl(subscriptionProductList.imageUrl())
                        .farmId(subscriptionProductList.farmId())
                        .farmName(subscriptionProductList.farmName())
                        .build())
                .toList();
    }

    public MemberSubsCancelRequest cancelSubscription(Long memberId, Long subsId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        Subscription subscription = subscriptionRepository.findByIdOrThrow(subsId);
        PackageProductResponse cancelPackage = productFeign.getPackageProduct(subscription.getPackageId());

        if(!subscription.getMember().getId().equals(memberId)) {
            throw new BaseCustomException(FORBIDDEN);
        }

        subscriptionRepository.delete(subscription);
        return MemberSubsCancelRequest.from(cancelPackage);
    }

    //== Kafka를 통한 정기 구독 비동기 처리 ==//
    @Transactional
    @KafkaListener(topics = "subscription-topic", groupId = "order-group",/*order group으로 부터 메시지가 들어오면*/ containerFactory = "kafkaListenerContainerFactory")
    public void consumeIssueNotification(String message /*listen 하면 스트링 형태로 메시지가 들어온다*/) {
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaSubscribeProductRequest subscribeRequest = null;
        try {
            subscribeRequest = objectMapper.readValue(message, KafkaSubscribeProductRequest.class);
            subscribePackageProduct(subscribeRequest);
        } catch (JsonProcessingException e) {
            throw new BaseCustomException(INVALID_SUBSCRIPTION_REQUEST);
        } catch(Exception e) {
            assert subscribeRequest != null;
            KafkaOrderRollbackRequest rollbackRequest = new KafkaOrderRollbackRequest(subscribeRequest.productId(), subscribeRequest.memberId(), subscribeRequest.couponId());
            sendRollbackOrderMessage(rollbackRequest);
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

    private void sendRollbackOrderMessage(KafkaOrderRollbackRequest subscribeRequest) {
        KafkaOrderRollbackRequest rollbackMessage = new KafkaOrderRollbackRequest(subscribeRequest.productId(), subscribeRequest.memberId(), subscribeRequest.couponId());
        kafkaTemplate.send("order-rollback-topic", rollbackMessage);
    }
}
