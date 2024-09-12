package org.samtuap.inong.domain.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.live.entity.Live;

import java.time.LocalDateTime;


@Entity
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "live_id")
    private Live live;

    private String content;
}
