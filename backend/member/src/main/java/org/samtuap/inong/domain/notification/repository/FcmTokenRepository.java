package org.samtuap.inong.domain.notification.repository;

import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.notification.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    List<FcmToken> findAllByMember(Member member);

    Optional<FcmToken> findByToken(String token);
}
