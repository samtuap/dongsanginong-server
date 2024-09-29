package org.samtuap.inong.domain.farmNotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.farmNotice.dto.*;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;
import org.samtuap.inong.domain.farmNotice.entity.NoticeComment;
import org.samtuap.inong.domain.farmNotice.repository.FarmNoticeImageRepository;
import org.samtuap.inong.domain.farmNotice.repository.FarmNoticeRepository;
import org.samtuap.inong.domain.farmNotice.repository.NoticeCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmNoticeService {

    private final FarmNoticeRepository farmNoticeRepository;
    private final FarmNoticeImageRepository farmNoticeImageRepository;
    private final FarmRepository farmRepository; // 농장 id 가져오기 위해 참조
    private final NoticeCommentRepository noticeCommentRepository;
    private final MemberFeign memberFeign;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 공지 목록 조회 => 제목, 내용, 사진(슬라이더)
     */
    public Page<NoticeListResponse> noticeList(Long id, Pageable pageable) {
        // 해당 id에 일치한 농장 가져오기
        Farm farm = farmRepository.findById(id).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        // 파라미터id와 일치한 농장의 notice 목록 가져와서 => dto로 변환
        Page<FarmNotice> noticeList = farmNoticeRepository.findByFarm(farm, pageable);

        return noticeList.map(notice -> {
            List<FarmNoticeImage> noticeImages = farmNoticeImageRepository.findByFarmNotice(notice);
            // 댓글 수 반환
            Page<NoticeComment> commentList = noticeCommentRepository.findByFarmNotice(notice, pageable);
            return NoticeListResponse.from(notice, noticeImages, commentList.getTotalElements());
        });
    }

    /**
     * 공지 디테일 조회 => 제목, 내용, 사진(슬라이더) + 댓글
     */
    public NoticeDetailResponse noticeDetail(Long farmId, Long noticeId) {
        // 해당 id에 일치하는 농장 가져오기
        Farm farm = farmRepository.findById(farmId).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        FarmNotice farmNotice = farmNoticeRepository.findByIdAndFarm(noticeId, farm);
        // 공지사항이 존재하지 않는 경우 예외 처리
        if (farmNotice == null) {
            throw new BaseCustomException(NOTICE_NOT_FOUND);
        }

        // 이미지repo에서 이미지 찾아오기
        List<FarmNoticeImage> noticeImages = farmNoticeImageRepository.findByFarmNotice(farmNotice);
        NoticeDetailResponse dto = NoticeDetailResponse.from(farmNotice, noticeImages);

        return dto;
    }

    /**
     * 유저가 공지글에 댓글 작성
     */
    @Transactional
    public void commentCreate(Long farmId, Long noticeId, String memberId, CommentCreateRequest dto) {
        // 해당 id에 일치하는 농장 가져오기
        Farm farm = farmRepository.findById(farmId).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        FarmNotice farmNotice = farmNoticeRepository.findByIdAndFarm(noticeId, farm);
        // 공지사항이 존재하지 않는 경우 예외 처리
        if (farmNotice == null) {
            throw new BaseCustomException(NOTICE_NOT_FOUND);
        }

        NoticeComment noticeComment = CommentCreateRequest.to(dto, farmNotice, Long.parseLong(memberId));
        noticeCommentRepository.save(noticeComment);
    }

    /**
     * 공지에 달린 댓글 조회
     */
    public Page<CommentListResponse> commentList(Long farmId, Long noticeId, Pageable pageable) {
        // 해당 id에 일치하는 농장 가져오기
        Farm farm = farmRepository.findById(farmId).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        FarmNotice farmNotice = farmNoticeRepository.findByIdAndFarm(noticeId, farm);
        // 공지사항이 존재하지 않는 경우 예외 처리
        if (farmNotice == null) {
            throw new BaseCustomException(NOTICE_NOT_FOUND);
        }

        // 해당 농장의 모든 댓글 가져오기
        Page<NoticeComment> noticeCommentList = noticeCommentRepository.findByFarmNotice(farmNotice, pageable);
        return noticeCommentList.map(comment -> {
            MemberDetailResponse member = memberFeign.getMemberById(comment.getMemberId());
            return CommentListResponse.from(comment, member.name());
        });
    }

    /**
     * 유저 > 공지에 달린 본인의 댓글 수정
     */
    @Transactional
    public void commentUpdate(Long commentId, String memberId, CommentUpdateRequest dto) {

        NoticeComment comment = noticeCommentRepository.findById(commentId).orElseThrow(
                () -> new BaseCustomException(COMMENT_NOT_FOUND)
        );
        // 댓글 작성자 확인
        if (!comment.getMemberId().equals(Long.parseLong(memberId))) {
            throw new BaseCustomException(UNAUTHORIZED_ACTION);
        }

        // 댓글 업데이트
        dto.updateEntity(comment);
    }

    /**
     * 유저 > 공지에 달린 본인의 댓글 삭제
     */
    public void commentDelete(Long commentId, String memberId) {

        NoticeComment comment = noticeCommentRepository.findById(commentId).orElseThrow(
                () -> new BaseCustomException(COMMENT_NOT_FOUND)
        );
        // 댓글 작성자 확인
        if (!comment.getMemberId().equals(Long.parseLong(memberId))) {
            throw new BaseCustomException(UNAUTHORIZED_ACTION);
        }

        noticeCommentRepository.deleteById(comment.getId());
    }


    /**
     * 공지 생성 (판매자가 공지 등록)
     */
    @Transactional
    public void createNotice(Long sellerId, NoticeCreateRequest dto) {
        Farm farm = farmRepository.findBySellerId(sellerId).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        FarmNotice farmNotice = NoticeCreateRequest.toEntity(dto, farm);
        farmNoticeRepository.save(farmNotice);

        // 이미지 저장
        saveNoticeImages(farmNotice, dto.imageUrls());

        // Kafka를 통한 알림 전송
        issueNotificationToFollowers(farm, dto);
    }


    private void issueNotificationToFollowers(Farm farm, NoticeCreateRequest dto) {
        FollowersGetResponse followers = memberFeign.getFollowers(farm.getId());
        for(Long memberId : followers.followers()) {
            KafkaNotificationRequest notification = KafkaNotificationRequest.builder()
                    .memberId(memberId)
                    .title(farm.getFarmName() + " 농장에 공지 글이 등록 되었어요!")
                    .content(dto.title())
                    .build();
        kafkaTemplate.send("send-notification-topic", notification);
        }
    }

    /**
     * 공지 수정 (판매자가 공지 수정)
     */
    @Transactional
    public void updateNotice(Long noticeId, Long sellerId, NoticeUpdateRequest dto) {
        Farm farm = farmRepository.findBySellerId(sellerId).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        FarmNotice farmNotice = farmNoticeRepository.findByIdAndFarm(noticeId, farm);
        if (farmNotice == null) {
            throw new BaseCustomException(NOTICE_NOT_FOUND);
        }

        updateFarmNoticeFields(farmNotice, dto);

        // 기존 이미지 삭제 및 새로운 이미지 저장
        farmNoticeImageRepository.deleteByFarmNotice(farmNotice);
        saveNoticeImages(farmNotice, dto.imageUrls());
    }

    private void updateFarmNoticeFields(FarmNotice farmNotice, NoticeUpdateRequest dto) {
        // 리플렉션을 통해 title과 contents 필드를 업데이트
        try {
            Field titleField = FarmNotice.class.getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(farmNotice, dto.title());

            Field contentsField = FarmNotice.class.getDeclaredField("contents");
            contentsField.setAccessible(true);
            contentsField.set(farmNotice, dto.content());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BaseCustomException(FIELD_NOT_FOUND);
        }
    }

    /**
     * 공지 이미지 저장
     */
    @Transactional
    public void saveNoticeImages(FarmNotice farmNotice, List<String> imageUrls) {
        List<FarmNoticeImage> farmNoticeImages = NoticeCreateRequest.toImageEntityList(farmNotice, imageUrls);
        farmNoticeImageRepository.saveAll(farmNoticeImages);
    }

    /**
     * 공지 삭제 (판매자가 공지 삭제)
     */
    @Transactional
    public void deleteNotice(Long noticeId, Long sellerId) {
        Farm farm = farmRepository.findBySellerId(sellerId).orElseThrow(
                () -> new BaseCustomException(FARM_NOT_FOUND)
        );

        FarmNotice farmNotice = farmNoticeRepository.findByIdAndFarm(noticeId, farm);
        if (farmNotice == null) {
            throw new BaseCustomException(NOTICE_NOT_FOUND);
        }

        // 관련 이미지 삭제
        farmNoticeImageRepository.deleteByFarmNotice(farmNotice);

        // 관련 댓글 삭제
        noticeCommentRepository.deleteByFarmNotice(farmNotice);

        // 공지 삭제
        farmNoticeRepository.delete(farmNotice);
    }


}
