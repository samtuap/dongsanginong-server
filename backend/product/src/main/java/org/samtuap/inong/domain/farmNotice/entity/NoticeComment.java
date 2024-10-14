package org.samtuap.inong.domain.farmNotice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE notice_comment SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder // CommentCreateRequest부분 때문에 builder 필요
@NoArgsConstructor
@AllArgsConstructor // builder를 사용해야해서 NoArgs와 AllArgs 필요
@Getter
public class NoticeComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private FarmNotice farmNotice;

    @NotNull
    @Column(columnDefinition = "varchar(300)")
    private String contents;

    private Long memberId; // member id => seller가 작성할 수도 있으니 null 가능

    private Long sellerId; // seller id => member가 작성할 수도 있으니 null 가능

    // 댓글 내용 수정
    public void updateContents(String newContents) {
        this.contents = newContents;
    }
}
