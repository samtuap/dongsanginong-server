package org.samtuap.inong.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.delivery.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.delivery.dto.MemberDetailResponse;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.order.dto.*;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.samtuap.inong.domain.receipt.repository.ReceiptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.samtuap.inong.common.exceptionType.OrderExceptionType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderBackOfficeService {
    private final ReceiptRepository receiptRepository;
    private final ProductFeign productFeign;
    private final MemberFeign memberFeign;
    private final OrderRepository orderRepository;

    public SalesDataGetResponse getSalesData(SalesTableGetRequest request, Long sellerId) {
        FarmDetailGetResponse farmInfo = productFeign.getFarmInfoWithSeller(sellerId);

        if(request.onlyFirstSubscription()) {
            return getVisualizationSalesDataOnlyFirst(farmInfo.id(), request);
        } else {
            return getVisualizationSalesData(farmInfo.id(), request);
        }
    }

    public Page<SalesElementGetResponse> getSalesList(Pageable pageable, SalesTableGetRequest request, Long sellerId) {
        FarmDetailGetResponse farmInfo = productFeign.getFarmInfoWithSeller(sellerId);
        Page<Receipt> receipts = null;
        if(!request.onlyFirstSubscription()) {
            receipts = receiptRepository.findAllByOrderFarmId(pageable, farmInfo.id(), request.startTime(), request.endTime());
        } else {
            receipts = receiptRepository.findAllByOrderFarmIdFirstOnly(pageable, farmInfo.id(), request.startTime(), request.endTime());
        }

        List<Long> packageIds = receipts.stream().map(r -> r.getOrder().getPackageId()).toList();
        List<Long> memberIds = receipts.stream().map(r -> r.getOrder().getMemberId()).toList();

        List<PackageProductResponse> packageProductList = productFeign.getPackageProductListContainDeleted(packageIds);
        List<MemberDetailResponse> memberList = memberFeign.getMemberByIdsContainDeleted(memberIds);


        return receipts.map(r -> {
            PackageProductResponse packageProduct = packageProductList.stream()
                    .filter(p -> p.id().equals(r.getOrder().getPackageId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseCustomException(INVALID_PACKAGE_ID));

            MemberDetailResponse memberDetail = memberList.stream()
                    .filter(m -> m.id().equals(r.getOrder().getMemberId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseCustomException(INVALID_MEMBER_ID));

            return SalesElementGetResponse.fromEntity(r, packageProduct, memberDetail);
        });
    }


    // 시각화를 위한 데이터 만들기
    private SalesDataGetResponse getVisualizationSalesData(Long farmId, SalesTableGetRequest request) {
        LocalDateTime curTime = request.startTime();
        LocalDateTime endTime = request.endTime();
        boolean isFirstOrder = request.onlyFirstSubscription();

        List<String> labels = new ArrayList<>();
        List<Long> monthSaleCount = new ArrayList<>();
        List<Long> monthSaleAmount = new ArrayList<>();
        Long totalCount = 0L, totalSalesAmount = 0L;
        while(!(curTime.getYear() == endTime.getYear() && curTime.getMonthValue() == endTime.getMonthValue())) {
            int year = curTime.getYear();
            int month = curTime.getMonthValue();
            SalesDataByYearAndMonth monthSaleData = receiptRepository.findSalesDataByYearAndMonth(farmId, year, month);

            if(monthSaleData.getAmount() == null) {
                monthSaleData.setAmount(0L);
            }

            labels.add(year+"."+month);
            monthSaleCount.add(monthSaleData.getCount());
            monthSaleAmount.add(monthSaleData.getAmount());

            totalCount += monthSaleData.getCount();
            totalSalesAmount += monthSaleData.getAmount();


            curTime = curTime.plusMonths(1);
        }

        return SalesDataGetResponse.builder()
                .labels(labels)
                .monthSaleAmount(monthSaleAmount)
                .monthSaleCount(monthSaleCount)
                .totalCount(totalCount)
                .totalSalesAmount(totalSalesAmount)
                .build();
    }

    // 시각화를 위한 데이터 만들기
    private SalesDataGetResponse getVisualizationSalesDataOnlyFirst(Long farmId, SalesTableGetRequest request) {
        LocalDateTime curTime = request.startTime();
        LocalDateTime endTime = request.endTime();
        boolean isFirstOrder = request.onlyFirstSubscription();

        List<String> labels = new ArrayList<>();
        List<Long> monthSaleCount = new ArrayList<>();
        List<Long> monthSaleAmount = new ArrayList<>();
        Long totalCount = 0L, totalSalesAmount = 0L;
        while(true) {
            int year = curTime.getYear();
            int month = curTime.getMonthValue();
            SalesDataByYearAndMonth monthSaleData = receiptRepository.findSalesDataByYearAndMonthFirstOnly(farmId, year, month, isFirstOrder);


            if(monthSaleData.getAmount() == null) {
                monthSaleData.setAmount(0L);
            }

            labels.add(year+"."+month);
            monthSaleCount.add(monthSaleData.getCount());
            monthSaleAmount.add(monthSaleData.getAmount());

            totalCount += monthSaleData.getCount();
            totalSalesAmount += monthSaleData.getAmount();

            if(year == endTime.getYear() && month == endTime.getMonthValue()) {
                break;
            }

            curTime = curTime.plusMonths(1);
        }

        return SalesDataGetResponse.builder()
                .labels(labels)
                .monthSaleAmount(monthSaleAmount)
                .monthSaleCount(monthSaleCount)
                .totalCount(totalCount)
                .totalSalesAmount(totalSalesAmount)
                .build();
    }

    public SalesDataWithPackagesGetResponse getSalesDataWithPackages(SalesTableGetRequest request, Long sellerId) {
        FarmDetailGetResponse farmInfo = productFeign.getFarmInfoWithSeller(sellerId);
        List<Long> packageIds = orderRepository.findAllByFarmIdAndBetweenStartAtAndEndAt(farmInfo.id(), request.startTime(), request.endTime());
        Map<Long, Long> countWithPackage = new HashMap<>();

        for (Long packageId : packageIds) {
            Long packageCount = countWithPackage.getOrDefault(packageId, 0L);
            countWithPackage.put(packageId, packageCount + 1);
        }

        List<PackageStatisticResponse> packageNames = productFeign.getPackageProductListContainDeletedNameOnly(packageIds);
        List<String> labels = new ArrayList<>();
        List<Long> count = new ArrayList<>();
//        List<Long> amount = new ArrayList<>();
        for(PackageStatisticResponse dto : packageNames) {
            labels.add(dto.getPackageName());
            count.add(countWithPackage.get(dto.getId()));
        }
        
        return new SalesDataWithPackagesGetResponse(labels, count);
    }
}
