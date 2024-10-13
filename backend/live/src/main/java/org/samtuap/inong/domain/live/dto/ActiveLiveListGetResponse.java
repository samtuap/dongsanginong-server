package org.samtuap.inong.domain.live.dto;

import lombok.Builder;
import org.samtuap.inong.domain.live.entity.Live;

@Builder
public record ActiveLiveListGetResponse(
        Long liveId,
        String sessionId,
        String farmName,
        String title,
        String liveImage,
        int participantCount,
        String category
) {

    public static ActiveLiveListGetResponse fromEntity(Live live, String farmName, int participantCount) {
        return ActiveLiveListGetResponse.builder()
                .liveId(live.getId())
                .sessionId(live.getSessionId())
                .farmName(farmName)
                .title(live.getTitle())
                .liveImage(live.getLiveImage())
                .participantCount(participantCount)
                .category(live.getCategory())
                .build();
    }
}

