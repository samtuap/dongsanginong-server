package org.samtuap.inong.domain.receipt.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.dto.ReceiptInfoResponse;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;

@RequiredArgsConstructor
@Service
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final OrderRepository orderRepository;
    private final ProductFeign productFeign;


    public ReceiptInfoResponse getReceiptInfo(Long orderId) {
        Ordering ordering = orderRepository.findById(orderId).orElseThrow(()-> new BaseCustomException(ORDER_NOT_FOUND));
        Receipt receipt = receiptRepository.findByOrderOrThrow(ordering);
        PackageProductResponse product = productFeign.getPackageProduct(ordering.getPackageId());
        return ReceiptInfoResponse.from(receipt, product);
    }
}
