package org.samtuap.inong.domain.notification.repository;

import org.samtuap.inong.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
