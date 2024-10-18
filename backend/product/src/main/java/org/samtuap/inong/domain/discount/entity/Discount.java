package org.samtuap.inong.domain.discount.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.samtuap.inong.domain.product.entity.PackageProduct;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer discount;

    private LocalDate startAt;

    private LocalDate endAt;

    private boolean discountActive; // 할인 활성화 상태

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private PackageProduct packageProduct;


    public void activateDiscount() {
        this.discountActive = true;
    }

    public void deactivateDiscount() {
        this.discountActive = false;
    }

}
