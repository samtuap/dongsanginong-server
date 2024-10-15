package org.samtuap.inong.domain.farm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.seller.dto.SellerFarmInfoUpdateRequest;

@Getter
@Entity
@SQLDelete(sql = "UPDATE farm SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Farm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long sellerId;

    @NotNull
    private String farmName;

    @NotNull
    @Column(columnDefinition = "varchar(5000)")
    private String bannerImageUrl;

    @NotNull
    @Column(columnDefinition = "varchar(5000)")
    private String profileImageUrl;

    @Column(columnDefinition = "varchar(5000)")
    private String farmIntro;

    @NotNull
    private Long favoriteCount;

    @NotNull
    private Long orderCount;

    public void updateInfo(SellerFarmInfoUpdateRequest infoRequest) {
        this.bannerImageUrl = infoRequest.bannerImageUrl();
        this.profileImageUrl = infoRequest.profileImageUrl();
        this.farmName = infoRequest.farmName();
        this.farmIntro = infoRequest.farmIntro();
    }

    public void updateFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void updateOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }
}
