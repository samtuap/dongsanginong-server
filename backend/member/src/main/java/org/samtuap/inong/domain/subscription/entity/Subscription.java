package org.samtuap.inong.domain.subscription.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.member.entity.Member;

import java.time.LocalDate;

@Getter
@Entity
@SQLDelete(sql = "UPDATE subscription SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long packageId;

    @NotNull
    private LocalDate payDate;
}
