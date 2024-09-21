package org.samtuap.inong.domain.farmNotice.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farmNotice.dto.*;
import org.samtuap.inong.domain.farmNotice.service.FarmNoticeService;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 유저가 공지글에 댓글 작성
     */
    @PostMapping("/{farm_id}/notice/{notice_id}/comment/create")
    public void commentCreate(@PathVariable("farm_id") Long farmId,
                              @PathVariable("notice_id") Long noticeId,
                              @RequestHeader("myId") String memberId,
                              @RequestBody CommentCreateRequest dto) {

        farmNoticeService.commentCreate(farmId, noticeId, memberId, dto);
    }

    /**
     * 공지에 달린 댓글 조회
     */
    @GetMapping("/{farm_id}/notice/{notice_id}/comment")
    public List<CommentListResponse> commentList(@PathVariable("farm_id") Long farmId,
                                                 @PathVariable("notice_id") Long noticeId) {

        return farmNoticeService.commentList(farmId, noticeId);
    }

    /**
     * 유저 > 공지에 달린 '본인'의 댓글 수정
     */
    @PutMapping("/notice/comment/{comment_id}")
    public void commentUpdate(@PathVariable("comment_id") Long commentId,
                              @RequestHeader("myId") String memberId,
                              @RequestBody CommentUpdateRequest dto) {

        farmNoticeService.commentUpdate(commentId, memberId, dto);
    }

    /**
     * 유저 > 공지에 달린 '본인'의 댓글 삭제
     */
    @DeleteMapping("/notice/comment/{comment_id}/delete")
    public void commentDelete(@PathVariable("comment_id") Long commentId,
                              @RequestHeader("myId") String memberId) {

        farmNoticeService.commentDelete(commentId, memberId);
    }

    /**
     * 공지 생성 (판매자가 공지 등록)
     */
    @PostMapping("/{farm_id}/notice/create")
    public void createNotice(@PathVariable("farm_id") Long farmId,
                             @RequestBody NoticeCreateRequest dto) {
        farmNoticeService.createNotice(farmId, dto);
    }

    /**
     * 공지 수정 (판매자가 공지 수정)
     */
    @PutMapping("/{farm_id}/notice/{notice_id}/update")
    public void updateNotice(@PathVariable("farm_id") Long farmId,
                             @PathVariable("notice_id") Long noticeId,
                             @RequestBody NoticeUpdateRequest dto) {
        farmNoticeService.updateNotice(farmId, noticeId, dto);
    }

    /**
     * 공지 삭제 (판매자가 공지 삭제)
     */
    @DeleteMapping("/{farm_id}/notice/{notice_id}/delete")
    public void deleteNotice(@PathVariable("farm_id") Long farmId,
                             @PathVariable("notice_id") Long noticeId) {
        farmNoticeService.deleteNotice(farmId, noticeId);
    }

}
