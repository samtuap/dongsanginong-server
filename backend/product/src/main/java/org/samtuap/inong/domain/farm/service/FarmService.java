package org.samtuap.inong.domain.farm.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farm.dto.FarmListGetResponse;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FarmService {
    private final FarmRepository farmRepository;

    // 최신순, 스크랩순, 판매량 순
    public Page<FarmListGetResponse> getFarmList(Pageable pageable) {
        return farmRepository.findAll(pageable).map(FarmListGetResponse::fromEntity);
    }
}
