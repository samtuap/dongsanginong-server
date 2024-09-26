package org.samtuap.inong.domain.order.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.receipt.entity.Receipt;

@Builder
public record OrderPaymentListResponse(Long packageId,
                                       String packageName,
                                       Long farmId,
                                       String farmName,
                                       String orderAt,
                                       String paymentMethod,
                                       Long totalPrice) {
    public static OrderPaymentListResponse from(Ordering ordering, PackageProductResponse product, Receipt receipt) {
        return OrderPaymentListResponse.builder()
                .packageId(product.id())
                .packageName(product.packageName())
                .farmId(product.farmId())
                .farmName(product.farmName())
                .orderAt(ordering.getCreatedAt().toString())
                .paymentMethod(receipt.getPaymentMethod().toString())
                .totalPrice(receipt.getTotalPrice())
                .build();
    }
}
