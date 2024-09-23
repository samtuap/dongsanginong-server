package org.samtuap.inong.domain.live.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;


@Entity
@Getter
@SQLDelete(sql = "UPDATE live SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Live extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmId;

    private LocalDateTime endAt;

    private String title;

    @Column(columnDefinition = "VARCHAR(3000)")
    private String liveImage;
}
