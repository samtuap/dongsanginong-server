package org.samtuap.inong.domain.live.dto;

import lombok.Builder;
import org.samtuap.inong.domain.live.entity.Live;

@Builder
public record ActiveLiveListGetResponse(
        Long liveId,
        String farmName,
        String title,
        String liveImage,
        int participantCount
) {

    public static ActiveLiveListGetResponse fromEntity(Live live, String farmName, int participantCount) {
        return ActiveLiveListGetResponse.builder()
                .liveId(live.getId())
                .farmName(farmName)
                .title(live.getTitle())
                .liveImage(live.getLiveImage())
                .participantCount(participantCount)
                .build();
    }
}

