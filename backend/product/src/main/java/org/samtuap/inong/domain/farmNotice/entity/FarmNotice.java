package org.samtuap.inong.domain.farmNotice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE farm_notice SET delYn = 'Y' WHERE id = ?")
public class FarmNotice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "varchar(5000)")
    private String contents;
}
