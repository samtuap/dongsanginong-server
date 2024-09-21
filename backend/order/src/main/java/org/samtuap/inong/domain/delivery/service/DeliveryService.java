package org.samtuap.inong.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.delivery.dto.DeliveryUpComingListResponse;
import org.samtuap.inong.domain.delivery.dto.MemberDetailResponse;
import org.samtuap.inong.domain.delivery.dto.PackageProductResponse;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.delivery.entity.DeliveryStatus;
import org.samtuap.inong.domain.delivery.repository.DeliveryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final MemberFeign memberFeign;
    private final ProductFeign productFeign;

    /**
     * 사장님 페이지 > 다가오는 배송 처리
     */
    public Page<DeliveryUpComingListResponse> upcomingDelivery(Pageable pageable) {

        // 오늘날짜 포함 5일 뒤까지 내역 중 BEFORE_DELIVERY인 데이터
        Page<Delivery> deliveries = deliveryRepository.findByDeliveryStatusAndDeliveryAtBefore(
                DeliveryStatus.BEFORE_DELIVERY, LocalDateTime.now().plusDays(4), pageable);

        return deliveries.map(delivery -> {
            // delivery > ordering > memberId > feignClient로 memberName 가져오기
            MemberDetailResponse member = memberFeign.getMemberById(delivery.getOrdering().getMemberId());
            // delivery > ordering > productId > feignClient로 productName 가져오기
            PackageProductResponse packageProduct = productFeign.getPackageProduct(delivery.getOrdering().getPackageId());
            // DeliveryUpComingListResponse 생성
            return DeliveryUpComingListResponse.from(delivery, member.name(), packageProduct.packageName());
        });
    }
}
