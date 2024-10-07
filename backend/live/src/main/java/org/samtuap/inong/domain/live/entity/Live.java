package org.samtuap.inong.domain.live.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

import java.time.LocalDateTime;


@Entity
@Getter
@SQLDelete(sql = "UPDATE live SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Live extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmId;

    private LocalDateTime endAt;

    private String title;

    @Column(columnDefinition = "VARCHAR(3000)")
    private String liveImage;

    private String sessionId; // sessionId 추가

    private Long ownerId;

    public void updateEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public void updateSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
