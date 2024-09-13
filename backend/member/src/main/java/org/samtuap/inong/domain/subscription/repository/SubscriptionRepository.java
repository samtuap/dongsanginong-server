package org.samtuap.inong.domain.subscription.repository;

import org.samtuap.inong.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
