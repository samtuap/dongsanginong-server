package org.samtuap.inong.domain.farmNotice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.farmNotice.dto.*;
import org.samtuap.inong.domain.farmNotice.service.FarmNoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/farm")
@RequiredArgsConstructor
public class FarmNoticeController {

    private final FarmNoticeService farmNoticeService;

    /**
     * 공지 목록 조회 => 제목, 내용, 사진(슬라이더)
     */
    @GetMapping("/no-auth/{farm_id}/notice/list")
    public ResponseEntity<Page<NoticeListResponse>> noticeList(@PathVariable("farm_id") Long id,
                                                               @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC)Pageable pageable) {
        return new ResponseEntity<>(farmNoticeService.noticeList(id, pageable), HttpStatus.OK);
    }

    /**
     * 공지 디테일 조회 => 제목, 내용, 사진(슬라이더) + 댓글
     */
    @GetMapping("/no-auth/{farm_id}/notice/{notice_id}")
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
                              @RequestHeader(value = "myId", required = false) Long memberId,
                              @RequestHeader(value = "sellerId", required = false) Long sellerId,
                              @RequestBody CommentCreateRequest dto) {
        farmNoticeService.commentCreate(farmId, noticeId, memberId, sellerId, dto);
    }

    /**
     * 공지에 달린 댓글 조회
     */
    @GetMapping("/no-auth/{farm_id}/notice/{notice_id}/comment")
    public ResponseEntity<Page<CommentListResponse>> commentList(@PathVariable("farm_id") Long farmId,
                                                                 @PathVariable("notice_id") Long noticeId,
                                                                 @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(farmNoticeService.commentList(farmId, noticeId, pageable), HttpStatus.OK);
    }

    /**
     * 유저 > 공지에 달린 '본인'의 댓글 수정
     */
    @PutMapping("/notice/comment/{comment_id}")
    public void commentUpdate(@PathVariable("comment_id") Long commentId,
                              @RequestHeader(value = "myId", required = false) Long memberId,
                              @RequestHeader(value = "sellerId", required = false) Long sellerId,
                              @RequestBody CommentUpdateRequest dto) {

        farmNoticeService.commentUpdate(commentId, memberId, sellerId, dto);
    }

    /**
     * 유저 > 공지에 달린 '본인'의 댓글 삭제
     */
    @DeleteMapping("/notice/comment/{comment_id}/delete")
    public void commentDelete(@PathVariable("comment_id") Long commentId,
                              @RequestHeader(value = "myId", required = false) Long memberId,
                              @RequestHeader(value = "sellerId", required = false) Long sellerId) {

        farmNoticeService.commentDelete(commentId, memberId, sellerId);
    }

    /**
     * 공지 생성 (판매자가 공지 등록)
     */
    @PostMapping("/notice/create")
    public void createNotice(@RequestHeader("sellerId") Long sellerId,
                             @RequestBody NoticeCreateRequest dto) {
        farmNoticeService.createNotice(sellerId, dto);
    }

    /**
     * 공지 수정 (판매자가 공지 수정)
     */
    @PutMapping("/notice/{notice_id}/update")
    public void updateNotice(@PathVariable("notice_id") Long noticeId,
                             @RequestHeader("sellerId") Long sellerId,
                             @RequestBody NoticeUpdateRequest dto) {
        farmNoticeService.updateNotice(noticeId, sellerId, dto);
    }

    /**
     * 공지 삭제 (판매자가 공지 삭제)
     */
    @DeleteMapping("/notice/{notice_id}/delete")
    public void deleteNotice(@PathVariable("notice_id") Long noticeId,
                             @RequestHeader("sellerId") Long sellerId) {
        farmNoticeService.deleteNotice(noticeId, sellerId);
    }

}
