package org.samtuap.inong.domain.notification.repository;

import org.samtuap.inong.domain.notification.entity.SellerNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerNotificationRepository extends JpaRepository<SellerNotification, Long> {
}
