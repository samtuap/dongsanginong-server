package org.samtuap.inong.domain.favorite.entity;

import jakarta.persistence.*;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.member.entity.Member;

@Entity
public class Favorites extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
