package org.samtuap.inong.domain.farmNotice.repository;

import org.samtuap.inong.domain.farmNotice.entity.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
}
