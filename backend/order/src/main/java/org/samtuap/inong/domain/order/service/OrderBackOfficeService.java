package org.samtuap.inong.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.OrderExceptionType;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.delivery.dto.MemberDetailResponse;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.dto.SalesElementGetResponse;
import org.samtuap.inong.domain.order.dto.SalesTableGetRequest;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderBackOfficeService {
    private final ReceiptRepository receiptRepository;
    private final OrderRepository orderRepository;
    private final ProductFeign productFeign;
    private final MemberFeign memberFeign;

    public SalesDataGetResponse getSalesData(SalesTableGetRequest request, Long sellerId) {

        FarmDetailGetResponse farmInfo = productFeign.getFarmInfoWithSeller(sellerId);
        SalesDataGetResponse salesData = null;
        if(!request.onlyFirstSubscription()) {
            salesData = receiptRepository.findSalesData(farmInfo.id(), request.startTime(), request.endTime());
        } else {
            salesData = receiptRepository.findSalesDataFirstOnly(farmInfo.id(), request.startTime(), request.endTime());
        }

        return salesData;
    }

    public List<SalesElementGetResponse> getSalesList(SalesTableGetRequest request, Long sellerId) {
        FarmDetailGetResponse farmInfo = productFeign.getFarmInfoWithSeller(sellerId);
        List<Receipt> receipts = null;
        if(!request.onlyFirstSubscription()) {
            receipts = receiptRepository.findAllByOrderFarmId(farmInfo.id(), request.startTime(), request.endTime());
        } else {
            receipts = receiptRepository.findAllByOrderFarmIdFirstOnly(farmInfo.id(), request.startTime(), request.endTime());
        }

        List<Long> packageIds = receipts.stream().map(r -> r.getOrder().getPackageId()).toList();
        List<Long> memberIds = receipts.stream().map(r -> r.getOrder().getMemberId()).toList();

        List<PackageProductResponse> packageProductList = productFeign.getPackageProductList(packageIds);
        List<MemberDetailResponse> memberList = memberFeign.getMemberByIds(memberIds);


        receipts.stream().map(r -> {
            PackageProductResponse packageProduct = packageProductList.stream()
                    .filter(p -> p.id().equals(r.getOrder().getPackageId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseCustomException(OrderExceptionType.INVALID_PACKAGE_PRODUCT));

            MemberDetailResponse memberDetail = memberList.stream()
                    .filter(p -> p.id().equals(r.getOrder().getPackageId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseCustomException(OrderExceptionType.INVALID_PACKAGE_PRODUCT));

            return SalesElementGetResponse.fromEntity(receipt, packageProduct, memberDetail);
        }).toList();
    }
}
