package org.samtuap.inong.domain.notification.repository;

import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Favorites, Long> {
}
