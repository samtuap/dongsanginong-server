package org.samtuap.inong.domain.receipt.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.receipt.entity.Receipt;

@Builder
public record ReceiptInfoResponse(Long orderId,
                                  String paidAt,
                                  Long productId,
                                  String productName,
                                  Long productPrice,
                                  Long farmId,
                                  String farmName,
                                  Long beforePrice,
                                  Long discountPrice,
                                  Long totalPrice,
                                  String paymentMethod,
                                  String paymentStatus
                                  ) {
    public static ReceiptInfoResponse from(Receipt receipt, PackageProductResponse product) {
        return ReceiptInfoResponse.builder()
                .orderId(receipt.getOrder().getId())
                .paidAt(receipt.getPaidAt().toString())
                .productId(product.id())
                .productName(product.packageName())
                .productPrice(product.price())
                .farmId(product.farmId())
                .farmName(product.farmName())
                .beforePrice(receipt.getBeforePrice())
                .discountPrice(receipt.getDiscountPrice())
                .totalPrice(receipt.getTotalPrice())
                .paymentMethod(receipt.getPaymentMethod().toString())
                .paymentStatus(receipt.getPaymentStatus().toString())
                .build();
    }
}
