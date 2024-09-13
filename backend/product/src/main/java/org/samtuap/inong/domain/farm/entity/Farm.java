package org.samtuap.inong.domain.farm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE farm SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class Farm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long sellerId;

    @NotNull
    private String farmName;

    @NotNull
    @Column(columnDefinition = "varchar(5000)")
    private String bannerImageUrl;

    @NotNull
    @Column(columnDefinition = "varchar(5000)")
    private String profileImageUrl;

    private String farmIntro;
}
