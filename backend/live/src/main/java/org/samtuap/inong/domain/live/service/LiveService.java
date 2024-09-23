package org.samtuap.inong.domain.live.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.client.FarmFeign;
import org.samtuap.inong.domain.live.dto.ActiveLiveListGetResponse;
import org.samtuap.inong.domain.live.dto.FarmResponse;
import org.samtuap.inong.domain.live.entity.Live;
import org.samtuap.inong.domain.live.repository.LiveRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LiveService {

    private final LiveRepository liveRepository;
    private final FarmFeign farmFeign;

    public List<ActiveLiveListGetResponse> getActiveLiveList() {

        List<Live> activeLiveList = liveRepository.findActiveLives();

        return activeLiveList.stream()
                .map(live -> {
                    FarmResponse farmResponse = farmFeign.getFarmById(live.getFarmId());
                    String farmName = farmResponse.farmName();
                    return ActiveLiveListGetResponse.fromEntity(live, farmName);
                }).collect(Collectors.toList());
    }
}

