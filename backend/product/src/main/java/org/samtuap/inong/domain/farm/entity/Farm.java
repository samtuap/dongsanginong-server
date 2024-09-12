package org.samtuap.inong.domain.farm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE farm SET delYn = 'Y' WHERE id = ?")
public class Farm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long sellerId;

    @NotNull
    private String farmName;

    @NotNull
    private String bannerImageUrl;

    @NotNull
    private String profileImageUrl;

    private String farmIntro;
}
