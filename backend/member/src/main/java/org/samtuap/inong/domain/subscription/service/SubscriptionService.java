package org.samtuap.inong.domain.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ProductFeign productFeign;

    @Transactional
    public void registerBillingKey(Long memberId, String billingKey) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        member.updateBillingKey(billingKey);
    }


    public SubscriptionListGetResponse getSubscriptionToPay() {
        List<Subscription> subscriptions = subscriptionRepository.findAllByPayDate(LocalDate.now());

        for (Subscription subscription : subscriptions) {
            log.info("line 36: {}, {}, {}", subscription.getId(), subscription.getPayDate(), subscription.getPackageId());

        }

        List<Long> packageProductIds = subscriptions.stream()
                .map(Subscription::getId)
                .toList();

        List<PackageProductResponse> packageProductInfos = productFeign.getPackageProductList(new PackageProductListGetRequest(packageProductIds));

        List<SubscriptionListGetResponse.SubscriptionGetResponse> list = subscriptions.stream()
                .map(SubscriptionListGetResponse.SubscriptionGetResponse::fromEntity)
                .toList();

        // 다음 결제일 변경
        updatePayDates(subscriptions);

        return new SubscriptionListGetResponse(list, packageProductInfos);
    }

    private void updatePayDates(List<Subscription> subscriptions) {
        subscriptions.forEach(s -> s.updatePayDate(s.getPayDate().plusDays(28)));
    }

}
