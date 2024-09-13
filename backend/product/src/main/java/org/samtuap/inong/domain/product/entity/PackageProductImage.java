package org.samtuap.inong.domain.product.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@SQLDelete(sql = "UPDATE package_product_image SET delYn = 'Y' WHERE id = ?")
public class PackageProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(32779)")
    private String imageUrl;
}
