package org.samtuap.inong.domain.farm.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
public class FarmCategoryRelation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private FarmCategory category;

}
