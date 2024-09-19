package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;

@Builder
public record NoticeImageDto(
        String imageUrl
) {
    // DTO를 엔티티로 변환하는 메서드
    public static FarmNoticeImage toEntity(NoticeImageDto dto, FarmNotice farmNotice) {
        return FarmNoticeImage.builder()
                .imageUrl(dto.imageUrl)
                .farmNotice(farmNotice)
                .build();
    }
}
