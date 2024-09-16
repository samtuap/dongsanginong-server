package org.samtuap.inong.domain.farmNotice.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farmNotice.dto.NoticeListResponse;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.FarmNoticeImage;
import org.samtuap.inong.domain.farmNotice.repository.FarmNoticeImageRepository;
import org.samtuap.inong.domain.farmNotice.repository.FarmNoticeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmNoticeService {

    private final FarmNoticeRepository farmNoticeRepository;
    private final FarmNoticeImageRepository farmNoticeImageRepository;

    /**
     * 공지 목록 조회 => 제목, 내용, 사진(슬라이더)
     */
    public List<NoticeListResponse> noticeList() {

        // 목록 가져와서 => dto로 변환
        List<FarmNotice> noticeList = farmNoticeRepository.findAll();
        List<NoticeListResponse> listDtos = new ArrayList<>();

        for (FarmNotice notice:noticeList) {

            List<FarmNoticeImage> noticeImages = farmNoticeImageRepository.findByFarmNotice(notice);
            NoticeListResponse dto = NoticeListResponse.from(notice, noticeImages);

            listDtos.add(dto);
        }
        return listDtos;
    }
}
