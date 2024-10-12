package org.samtuap.inong.domain.live.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.chat.websocket.SocketController;
import org.samtuap.inong.domain.live.dto.*;
import org.samtuap.inong.domain.live.entity.Live;
import org.samtuap.inong.domain.live.repository.LiveRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.samtuap.inong.common.client.FarmFeign;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.samtuap.inong.common.exceptionType.LiveExceptionType.SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveService {

    private final LiveRepository liveRepository;
    private final FarmFeign farmFeign;
    private final OpenVidu openVidu;
    private final SocketController socketController;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * feign 요청용
     */
    @Transactional
    public List<FavoritesLiveListResponse> getFavoritesFarmLiveList(List<Long> favoriteFarmList) {
        // favoriteFarmList => 즐겨찾기 한 농장 id만 담겨있는 list
        List<Live> liveList = liveRepository.findByFarmIdInAndEndAtIsNull(favoriteFarmList);
        List<FavoritesLiveListResponse> list = new ArrayList<>();

        for (Live live: liveList) {
            int participantCount = socketController.getParticipantCount(live.getSessionId());
            FavoritesLiveListResponse dto = FavoritesLiveListResponse.from(live, participantCount);
            list.add(dto);
        }
        return list;
    }

    public Page<ActiveLiveListGetResponse> getActiveLiveList(Pageable pageable) {

        Page<Live> activeLiveList = liveRepository.findActiveLives(pageable);

        return activeLiveList.map(live -> {
            FarmResponse farmResponse = farmFeign.getFarmById(live.getFarmId());
            String farmName = farmResponse.farmName();
            int participantCount = socketController.getParticipantCount(live.getSessionId());
            return ActiveLiveListGetResponse.fromEntity(live, farmName, participantCount);
        });
    }

    /**
     * 라이브 스트리밍 시작 및 db에 기록 저장
     */
    @Transactional
    public LiveSessionResponse createLiveSession(Long sellerId, LiveSessionRequest request) throws Exception {
        // sellerId로 farmId 가져오기 => feign
        FarmDetailGetResponse farm = farmFeign.getFarmInfoWithSeller(sellerId);

        Live live = Live.builder()
                        .farmId(farm.id())
                        .ownerId(sellerId)
                        .title(request.title())
                        .liveImage(request.liveImage())
                        .build();
        liveRepository.save(live); // live 시작 > 먼저 디비에 저장

        // OpenVidu 세션 생성
        SessionProperties properties = SessionProperties.fromJson(new HashMap<>()).build();
        Session session = openVidu.createSession(properties);

        live.updateSessionId(session.getSessionId()); // sessionId를 라이브 시작하고 받아서 저장
        liveRepository.save(live);

        return LiveSessionResponse.fromEntity(request, live, session);
    }

    public LiveSessionResponse getSessionIdByLiveId(Long id) {
        Live live = liveRepository.findByIdOrThrow(id);
        if (live.getSessionId() == null) {
            throw new BaseCustomException(SESSION_NOT_FOUND);
        }
        log.info("{}로 session id 받자 : {}", id, live.getSessionId());
        return LiveSessionResponse.liveFromEntity(live);  // 라이브의 세션 ID 반환
    }

    /**
     * session 종료
     */
    @Transactional
    public void leaveSession(String sessionId) {
        // 방송이 종료되었을 때, 해당 세션의 종료 시간을 기록 (endAt)
        Live live = liveRepository.findBySessionIdOrThrow(sessionId);
        live.updateEndAt(LocalDateTime.now());
        redisTemplate.expire("live:participants:" + sessionId, 1, TimeUnit.HOURS);
        redisTemplate.expire("kicked:users:" + sessionId, 1, TimeUnit.HOURS);
        liveRepository.save(live);
    }
}
