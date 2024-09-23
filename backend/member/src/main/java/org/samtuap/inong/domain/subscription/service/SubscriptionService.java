package org.samtuap.inong.domain.subscription.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.favorites.repository.FavoritesRepository;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final MemberRepository memberRepository;

    @Transactional
    public void registerBillingKey(Long memberId, String billingKey) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        member.updateBillingKey(billingKey);
    }


}
