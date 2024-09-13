package org.samtuap.inong.domain.seller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
@SQLDelete(sql = "UPDATE seller SET delYn = 'Y' WHERE id = ?")
public class Seller extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String businessNumber;

    @NotNull
    private String businessName;

    @Column(columnDefinition = "varchar(5000)")
    private String contents;
}
