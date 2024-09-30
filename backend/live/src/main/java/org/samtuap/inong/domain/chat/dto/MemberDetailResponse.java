package org.samtuap.inong.domain.chat.dto;

public record MemberDetailResponse(
        Long id,
        String name,
        String email) {
}