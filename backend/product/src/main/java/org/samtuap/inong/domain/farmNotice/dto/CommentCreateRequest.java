package org.samtuap.inong.domain.farmNotice.dto;

import lombok.Builder;
import org.samtuap.inong.domain.farmNotice.entity.FarmNotice;
import org.samtuap.inong.domain.farmNotice.entity.NoticeComment;

@Builder
public record CommentCreateRequest(
        Long id,
        String contents
) {
    public static NoticeComment to(CommentCreateRequest commentCreateRequest, FarmNotice farmNotice, Long memberId, Long sellerId) {
        return NoticeComment.builder()
                .id(commentCreateRequest.id)
                .contents(commentCreateRequest.contents)
                .memberId(memberId) // 추후에 로그인한 회원의 정보를 가져오면 되기 때문에 변경될 예정 ⭐
                .sellerId(sellerId)
                .farmNotice(farmNotice)
                .build();
    }
}
