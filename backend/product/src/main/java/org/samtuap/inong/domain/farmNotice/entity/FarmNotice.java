package org.samtuap.inong.domain.farmNotice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE farm_notice SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Getter
public class FarmNotice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "varchar(5000)")
    private String contents;
}
