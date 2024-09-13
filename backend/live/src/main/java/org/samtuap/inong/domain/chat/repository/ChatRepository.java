package org.samtuap.inong.domain.chat.repository;

import org.samtuap.inong.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
