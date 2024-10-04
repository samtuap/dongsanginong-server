package org.samtuap.inong.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.springframework.data.domain.Page;

@Builder
public record SellerPackageListGetResponse(
        @NotNull Long id,
        @NotNull String packageName,
        @NotNull Long price,
        @NotNull String productCode
) {
    // PackageProduct 엔티티를 기반으로 DTO를 생성하는 메서드
    public static SellerPackageListGetResponse fromEntity(PackageProduct packageProduct) {
        return SellerPackageListGetResponse.builder()
                .id(packageProduct.getId())
                .packageName(packageProduct.getPackageName())
                .price(packageProduct.getPrice())
                .productCode(packageProduct.getProductCode())
                .build();
    }

    public static Page<SellerPackageListGetResponse> fromEntities(Page<PackageProduct> packageProducts) {
        return packageProducts.map(SellerPackageListGetResponse::fromEntity);
    }

}
