package org.samtuap.inong.domain.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.farm.entity.Farm;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE package_product SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class PackageProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm;

    @NotNull
    private String packageName;

    @NotNull
    private Integer delivery_cycle;

    @NotNull
    private Long price;

    private String productDescription;

    private String productCode;

    public void updatePackageName(String packageName) {
        this.packageName = packageName;
    }

    public void updateDeliveryCycle(Integer deliveryCycle) {
        this.delivery_cycle = deliveryCycle;
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updateProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
