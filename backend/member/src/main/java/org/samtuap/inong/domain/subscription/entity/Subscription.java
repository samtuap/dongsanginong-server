package org.samtuap.inong.domain.subscription.entity;

import jakarta.persistence.*;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.member.entity.Member;

@Entity
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Long packageId;
}
