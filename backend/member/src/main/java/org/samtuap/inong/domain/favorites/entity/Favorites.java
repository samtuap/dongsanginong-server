package org.samtuap.inong.domain.favorites.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.member.entity.Member;

@Getter
@Entity
@SQLDelete(sql = "UPDATE favorites SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Favorites extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
