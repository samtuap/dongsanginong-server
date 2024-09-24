package org.samtuap.inong.domain.subscription.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.subscription.dto.SubscriptionListGetResponse;
import org.samtuap.inong.domain.subscription.entity.Subscription;
import org.samtuap.inong.domain.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
        List<Subscription> subscriptions = subscriptionRepository.findAllByPayDateAndCanceledAtIsNull(LocalDate.now());
        List<SubscriptionListGetResponse.SubscriptionGetResponse> list = subscriptions.stream()
                .map(SubscriptionListGetResponse.SubscriptionGetResponse::fromEntity)
                .toList();

        return new SubscriptionListGetResponse(list);
    }
}
