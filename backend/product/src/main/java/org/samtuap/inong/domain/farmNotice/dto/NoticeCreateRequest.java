package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;

import java.util.List;

@Builder
public record NoticeCreateRequest(
        String title,
        String content,
        List<String> imageUrls
) {
    // DTO를 엔티티로 변환하는 메서드
    public static FarmNotice toEntity(NoticeCreateRequest dto, Farm farm) {
        return FarmNotice.builder()
                .title(dto.title)
                .contents(dto.content)
                .farm(farm)
                .build();
    }
}
