package org.samtuap.inong.domain.farmNotice.repository;

import feign.Param;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FarmNoticeImageRepository extends JpaRepository<FarmNoticeImage, Long> {

    List<FarmNoticeImage> findByFarmNotice(FarmNotice farmNotice);

    void deleteByFarmNotice(FarmNotice farmNotice);

    @Query("SELECT fni.imageUrl FROM FarmNoticeImage fni WHERE fni.farmNotice = :farmNotice")
    List<String> findImageUrlsByFarmNotice(@Param("farmNotice") FarmNotice farmNotice);
}
