package org.samtuap.inong.domain.live.dto;

import org.samtuap.inong.domain.live.entity.Live;

public record ActiveLiveListGetResponse(
        Long liveId,
        String farmName,
        String title,
        String liveImage
) {

    public static ActiveLiveListGetResponse fromEntity(Live live, String farmName) {
        return new ActiveLiveListGetResponse(
                live.getId(),
                farmName,
                live.getTitle(),
                live.getLiveImage()
        );
    }
}

