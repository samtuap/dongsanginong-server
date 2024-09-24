package org.samtuap.inong.domain.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.SubscriptionExceptionType;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.subscription.dto.PackageProductListGetRequest;
import org.samtuap.inong.domain.subscription.dto.SubscriptionListGetResponse;
import org.samtuap.inong.domain.subscription.entity.Subscription;
import org.samtuap.inong.domain.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
}
