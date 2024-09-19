package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;

import java.util.ArrayList;
import java.util.List;

@Builder
public record NoticeCreateRequest(
        String title,
        String content,
        List<String> imageUrls
) {
    // DTO를 엔티티로 변환하는 메서드
    public static FarmNotice toEntity(NoticeCreateRequest dto, Farm farm) {
        return FarmNotice.builder()
                .title(dto.title)
                .contents(dto.content)
                .farm(farm)
                .build();
    }

    // 여러 이미지 URL 리스트를 FarmNoticeImage 엔티티 리스트로 변환하는 메서드
    public static List<FarmNoticeImage> toImageEntityList(FarmNotice farmNotice, List<String> imageUrls) {
        List<FarmNoticeImage> farmNoticeImages = new ArrayList<>();
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                farmNoticeImages.add(FarmNoticeImage.builder()
                        .farmNotice(farmNotice)
                        .imageUrl(imageUrl)
                        .build());
            }
        }
        return farmNoticeImages;
    }
}
