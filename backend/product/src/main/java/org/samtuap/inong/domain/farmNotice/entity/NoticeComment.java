package org.samtuap.inong.domain.farmNotice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE notice_comment SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
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

    @NotNull
    private Long memberId; // 회원 id 컬럼

}
