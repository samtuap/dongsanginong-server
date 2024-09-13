package org.samtuap.inong.domain.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.live.entity.Live;

import java.time.LocalDateTime;


@Entity
@SQLDelete(sql = "UPDATE chat SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_id")
    private Live live;

    private String content;
}
