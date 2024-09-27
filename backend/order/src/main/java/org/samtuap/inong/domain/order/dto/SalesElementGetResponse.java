package org.samtuap.inong.domain.order.dto;

import lombok.Builder;
import org.samtuap.inong.domain.delivery.dto.MemberDetailResponse;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

// 매출 내역 단건 => Page로 반환
@Builder
public record SalesElementGetResponse(Long orderId,
                                      LocalDateTime paidAt,
                                      Long packageId,
                                      String packageName,
                                      Long paidAmount,
                                      Long discountAmount,
                                      Long beforeAmount,
                                      Long customerId,
                                      String customerName) {
    public static SalesElementGetResponse fromEntity(Receipt receipt, PackageProductResponse packageProduct, MemberDetailResponse memberDetail) {
        return SalesElementGetResponse.builder()
                .orderId(receipt.getOrder().getId())
                .paidAt(receipt.getPaidAt())
                .packageId(receipt.getOrder().getPackageId())
                .packageName(packageProduct.packageName())
                .customerId(memberDetail.id())
                .customerName(memberDetail.name())
                .paidAmount(receipt.getTotalPrice())
                .discountAmount(receipt.getDiscountPrice())
                .beforeAmount(receipt.getBeforePrice())
                .build();
    }
}
