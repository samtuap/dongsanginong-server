package org.samtuap.inong.domain.live.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;


@Entity
@SQLDelete(sql = "UPDATE live SET delYn = 'Y' WHERE id = ?")
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
