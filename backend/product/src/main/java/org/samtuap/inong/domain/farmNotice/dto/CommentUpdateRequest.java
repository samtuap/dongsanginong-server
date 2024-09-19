package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farmNotice.entity.NoticeComment;

@Builder
public record CommentUpdateRequest(
        Long id,
        String contents // 댓글 수정은 내용만 가능
) {
    public void updateEntity(NoticeComment comment) {
        comment.updateContents(this.contents);
    }
}
