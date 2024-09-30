package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;

import java.util.List;

@Builder
public record NoticeDetailResponse(
        Long id,
        String title,
        String content,
        List<String> noticeImages,
        int commentCnt
) {

    public static NoticeDetailResponse from(FarmNotice notice, List<FarmNoticeImage> noticeImages, int commentCnt) {

        List<String> imageUrls = noticeImages.stream()
                .map(FarmNoticeImage::getImageUrl)
                .toList();

        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContents())
                .noticeImages(imageUrls)
                .commentCnt(commentCnt)
                .build();
    }
}
