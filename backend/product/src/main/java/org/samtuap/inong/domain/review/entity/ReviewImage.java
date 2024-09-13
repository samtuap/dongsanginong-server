package org.samtuap.inong.domain.review.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE review_image SET delYn = 'Y' WHERE id = ?")
public class ReviewImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(32779)")
    private String imageUrl;
}
