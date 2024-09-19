package org.samtuap.inong.domain.farmNotice.repository;

import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmNoticeImageRepository extends JpaRepository<FarmNoticeImage, Long> {

    List<FarmNoticeImage> findByFarmNotice(FarmNotice farmNotice);

    void deleteByFarmNotice(FarmNotice farmNotice);
}
