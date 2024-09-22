package org.samtuap.inong.domain.farmNotice.dto;


import lombok.Getter;

import java.util.List;

@Getter
public record FollowersGetResponse(List<Long> followers) {
}
