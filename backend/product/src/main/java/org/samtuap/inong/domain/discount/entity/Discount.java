package org.samtuap.inong.domain.discount.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.samtuap.inong.domain.product.entity.PackageProduct;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
}
