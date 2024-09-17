package org.samtuap.inong.domain.farmNotice.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farmNotice.dto.NoticeDetailResponse;
import org.samtuap.inong.domain.farmNotice.dto.NoticeListResponse;
import org.samtuap.inong.domain.farmNotice.service.FarmNoticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/farm")
@RequiredArgsConstructor
public class FarmNoticeController {

    private final FarmNoticeService farmNoticeService;

    /**
     * 공지 목록 조회 => 제목, 내용, 사진(슬라이더)
     */
    @GetMapping("/{farm_id}/notice/list")
    public List<NoticeListResponse> noticeList(@PathVariable("farm_id") Long id) {

        return farmNoticeService.noticeList(id);
    }

    /**
     * 공지 디테일 조회 => 제목, 내용, 사진(슬라이더) + 댓글
     */
    @GetMapping("/{farm_id}/notice/{notice_id}")
    public NoticeDetailResponse noticeDetail(@PathVariable("farm_id") Long farmId,
                                           @PathVariable("notice_id") Long noticeId) {

        return farmNoticeService.noticeDetail(farmId, noticeId);
    }
}
