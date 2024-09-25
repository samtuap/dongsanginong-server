package org.samtuap.inong.domain.receipt.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.dto.ReceiptInfoResponse;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Service
public class ReceiptService {
    private ReceiptRepository receiptRepository;
    private OrderRepository orderRepository;
    private ProductFeign productFeign;


    public ReceiptInfoResponse getReceiptInfo(Long orderId) {
        Ordering ordering = orderRepository.findByIdOrThrow(orderId);
        Receipt receipt = receiptRepository.findByOrderOrThrow(ordering);
        PackageProductResponse product = productFeign.getPackageProduct(ordering.getPackageId());
        return ReceiptInfoResponse.from(receipt, product);
    }
}
