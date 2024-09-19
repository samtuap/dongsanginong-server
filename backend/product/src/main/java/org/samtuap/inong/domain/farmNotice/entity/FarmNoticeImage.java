package org.samtuap.inong.domain.farmNotice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
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
    private FarmNotice farmNotice;

    @Column(columnDefinition = "varchar(5000)")
    private String imageUrl;


    // 기본 생성자
    public FarmNoticeImage() {
    }

    // 생성자
    public FarmNoticeImage(String imageUrl, FarmNotice farmNotice) {
        this.imageUrl = imageUrl;
        this.farmNotice = farmNotice;
    }

}
