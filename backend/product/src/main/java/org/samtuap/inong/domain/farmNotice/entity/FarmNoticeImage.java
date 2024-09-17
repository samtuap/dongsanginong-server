package org.samtuap.inong.domain.farmNotice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE farm_notice_image SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Getter
public class FarmNoticeImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    @JsonIgnore // 직렬화에서 FarmNotice 제외
    private FarmNotice farmNotice;

    @Column(columnDefinition = "varchar(5000)")
    private String imageUrl;
}
