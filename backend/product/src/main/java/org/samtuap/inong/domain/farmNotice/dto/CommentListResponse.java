package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farmNotice.entity.NoticeComment;

@Builder
public record CommentListResponse(
    Long id,
    String name,
    String contents,
    String createdAt,
    Long memberId,
    Long sellerId
) {
    public static CommentListResponse from(NoticeComment comment, String name, Long memberId, Long sellerId) {
        return CommentListResponse.builder()
                .id(comment.getId())
                .name(name)
                .contents(comment.getContents())
                .createdAt(String.valueOf(comment.getCreatedAt()))
                .memberId(memberId)
                .sellerId(sellerId)
                .build();
    }
}
