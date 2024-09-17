package org.samtuap.inong.domain.farmNotice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.farmNotice.dto.NoticeDetailResponse;
import org.samtuap.inong.domain.farmNotice.dto.NoticeListResponse;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;
import org.samtuap.inong.domain.farmNotice.repository.FarmNoticeImageRepository;
import org.samtuap.inong.domain.farmNotice.repository.FarmNoticeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FarmNoticeService {

    private final FarmNoticeRepository farmNoticeRepository;
    private final FarmNoticeImageRepository farmNoticeImageRepository;
    private final FarmRepository farmRepository; // 농장 id 가져오기 위해 참조

    /**
     * 공지 목록 조회 => 제목, 내용, 사진(슬라이더)
     */
    public List<NoticeListResponse> noticeList(Long id) {

        // 해당 id에 일치한 농장 가져오기
        Farm farm = farmRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 id의 농장이 존재하지 않습니다.")
        );

        // 파라미터id와 일치한 농장의 notice 목록 가져와서 => dto로 변환
        List<FarmNotice> noticeList = farmNoticeRepository.findByFarm(farm);
        List<NoticeListResponse> listDtos = new ArrayList<>();

        for (FarmNotice notice:noticeList) {

            List<FarmNoticeImage> noticeImages = farmNoticeImageRepository.findByFarmNotice(notice);
            NoticeListResponse dto = NoticeListResponse.from(notice, noticeImages);

            listDtos.add(dto);
        }
        return listDtos;
    }

    /**
     * 공지 디테일 조회 => 제목, 내용, 사진(슬라이더) + 댓글
     */
    public NoticeDetailResponse noticeDetail(Long farmId, Long noticeId) {

        // 해당 id에 일치하는 농장 가져오기
        Farm farm = farmRepository.findById(farmId).orElseThrow(
                () -> new EntityNotFoundException("해당 id의 농장이 존재하지 않습니다.")
        );

        FarmNotice farmNotice = farmNoticeRepository.findByIdAndFarm(noticeId, farm);
        // 공지사항이 존재하지 않는 경우 예외 처리
        if (farmNotice == null) {
            throw new EntityNotFoundException("해당 농장에 해당하는 공지사항이 존재하지 않습니다.");
        }

        // 이미지repo에서 이미지 찾아오기
        List<FarmNoticeImage> noticeImages = farmNoticeImageRepository.findByFarmNotice(farmNotice);
        NoticeDetailResponse dto = NoticeDetailResponse.from(farmNotice, noticeImages);

        return dto;
    }
}
