package org.samtuap.inong.domain.notification.repository;

import org.samtuap.inong.domain.favorites.entity.Favorites;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByMemberId(Pageable pageable, Long memberId);

    Page<Notification> findAllByMemberIdAndIsRead(Pageable pageable, Long memberId, boolean isRead);

    List<Notification> findAllByMemberIdAndIsRead(Long memberId, boolean isRead);
}
