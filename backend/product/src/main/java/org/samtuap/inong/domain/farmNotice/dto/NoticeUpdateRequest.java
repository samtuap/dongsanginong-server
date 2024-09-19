package org.samtuap.inong.domain.farmNotice.dto;

import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;

import java.util.List;

public record NoticeUpdateRequest(
        String title,
        String content,
        List<String> imageUrls
) {
    // DTO를 기반으로 엔티티를 업데이트하는 메서드
    public static FarmNotice toEntity(NoticeUpdateRequest dto, FarmNotice farmNotice) {
        return FarmNotice.builder()
                .id(farmNotice.getId())   // 기존 엔티티의 ID 유지
                .title(dto.title)          // 새로 업데이트할 제목
                .contents(dto.content)     // 새로 업데이트할 내용
                .farm(farmNotice.getFarm())// 기존 엔티티의 농장 유지
                .build();
    }
}
