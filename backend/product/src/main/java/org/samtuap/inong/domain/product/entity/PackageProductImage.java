package org.samtuap.inong.domain.product.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLDelete(sql = "UPDATE package_product_image SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class PackageProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(5000)")
    private String imageUrl;
}
