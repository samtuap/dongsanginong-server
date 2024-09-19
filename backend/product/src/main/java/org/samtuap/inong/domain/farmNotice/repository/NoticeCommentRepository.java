package org.samtuap.inong.domain.farmNotice.repository;

import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {

    List<NoticeComment> findByFarmNotice(FarmNotice farmNotice);

    void deleteByFarmNotice(FarmNotice farmNotice);

    NoticeComment findByFarmNoticeAndId(FarmNotice farmNotice, Long id);
}
