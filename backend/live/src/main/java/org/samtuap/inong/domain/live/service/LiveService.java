package org.samtuap.inong.domain.live.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.live.dto.FavoritesLiveListResponse;
import org.samtuap.inong.domain.live.entity.Live;
import org.samtuap.inong.domain.live.repository.LiveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveService {

    private final LiveRepository liveRepository;

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
}
