package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NoticeCreateRequest(
        String title,
        String content,
        List<String> imageUrls
) {

}
