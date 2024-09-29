package org.samtuap.inong.domain.live.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.live.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.live.entity.Live;
import org.samtuap.inong.domain.live.repository.LiveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.samtuap.inong.common.client.FarmFeign;
import org.samtuap.inong.domain.live.dto.ActiveLiveListGetResponse;
import org.samtuap.inong.domain.live.dto.FarmResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveService {

    private final LiveRepository liveRepository;
    private final FarmFeign farmFeign;

    /**
     * feign 요청용
     */
    @Transactional
    public List<FavoritesLiveListResponse> getFavoritesFarmLiveList(List<Long> favoriteFarmList) {
        // favoriteFarmList => 즐겨찾기 한 농장 id만 담겨있는 list
        List<Live> liveList = liveRepository.findByFarmIdInAndEndAtIsNull(favoriteFarmList);
        List<FavoritesLiveListResponse> list = new ArrayList<>();

        for (Live live: liveList) {
            FavoritesLiveListResponse dto = FavoritesLiveListResponse.from(live);
            list.add(dto);
        }
        return list;
    }

    public List<ActiveLiveListGetResponse> getActiveLiveList() {

        List<Live> activeLiveList = liveRepository.findActiveLives();
        List<ActiveLiveListGetResponse> responseList = new ArrayList<>();

        for (Live live : activeLiveList) {
            FarmResponse farmResponse = farmFeign.getFarmById(live.getFarmId());
            String farmName = farmResponse.farmName();

            ActiveLiveListGetResponse response = ActiveLiveListGetResponse.fromEntity(live, farmName);
            responseList.add(response);
        }

        return responseList;
    }

}
