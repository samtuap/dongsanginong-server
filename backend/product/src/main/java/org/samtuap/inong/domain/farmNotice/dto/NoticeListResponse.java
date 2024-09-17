package org.samtuap.inong.domain.farmNotice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeListResponse {

    private Long id;

    private String title;

    private String content;

    // 공지 이미지 담을 리스트
    private List<FarmNoticeImage> noticeImages;

    public static NoticeListResponse from(FarmNotice notice, List<FarmNoticeImage> noticeImages) {
        return NoticeListResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContents())
                .noticeImages(noticeImages)
                .build();
    }

}
