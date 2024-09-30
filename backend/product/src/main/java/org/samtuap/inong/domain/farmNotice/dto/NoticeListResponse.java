package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;

import java.util.List;

@Builder
public record NoticeListResponse(
        Long id,
        String title,
        String content,
        List<String> noticeImages,
        Long commentCnt // 해동 공지사항의 댓글 수
) {

    public static NoticeListResponse from(FarmNotice notice, List<FarmNoticeImage> noticeImages, Long commentCnt) {

        List<String> imageUrls = noticeImages.stream()
                .map(FarmNoticeImage::getImageUrl)
                .toList();

        return NoticeListResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContents())
                .noticeImages(imageUrls)
                .commentCnt(commentCnt)
                .build();
    }
}
