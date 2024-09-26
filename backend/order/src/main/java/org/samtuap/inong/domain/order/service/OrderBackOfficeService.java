package org.samtuap.inong.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.dto.SalesTableGetRequest;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderBackOfficeService {
    private final ReceiptRepository receiptRepository;
    private final ProductFeign productFeign;
    public SalesDataGetResponse getSalesData(SalesTableGetRequest request, Long sellerId) {

        log.info("line 20 {}", request);

        FarmDetailGetResponse farmInfo = productFeign.getFarmInfoWithSeller(sellerId);
        SalesDataGetResponse salesData = null;
        if(!request.onlyFirstSubscription()) {
            salesData = receiptRepository.findSalesData(farmInfo.id(), request.startTime(), request.endTime());
        } else {
            salesData = receiptRepository.findSalesDataFirstOnly(farmInfo.id(), request.startTime(), request.endTime());
        }

        log.info("line 16: {}", salesData);

        return salesData;
    }

}
