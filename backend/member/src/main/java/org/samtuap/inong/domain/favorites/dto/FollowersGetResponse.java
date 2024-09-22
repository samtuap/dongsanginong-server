package org.samtuap.inong.domain.favorites.dto;

import lombok.Getter;

import java.util.List;

@Getter
public record FollowersGetResponse(List<Long> followers) {
}
