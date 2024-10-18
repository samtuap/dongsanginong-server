package org.samtuap.inong.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.common.request.KafkaNotificationRequest;
import org.samtuap.inong.domain.delivery.dto.*;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.delivery.entity.DeliveryStatus;
import org.samtuap.inong.domain.delivery.repository.DeliveryRepository;
import org.samtuap.inong.domain.delivery.dto.DeliveryListResponse;
import org.samtuap.inong.domain.order.entity.Ordering;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MemberFeign memberFeign;
    private final ProductFeign productFeign;
    private final KafkaTemplate kafkaTemplate;

    /**
     * 사장님 페이지 > 다가오는 배송 처리
     */
    public Page<DeliveryUpComingListResponse> upcomingDelivery(Long sellerId, Pageable pageable) {
        // 사장이 내농장 찾아옴
        FarmDetailGetResponse farm = productFeign.getFarmInfoWithSeller(sellerId);
        List<Ordering> orderingList = orderRepository.findByFarmId(farm.id());

        // 오늘날짜 포함 5일 뒤까지 내역 중 BEFORE_DELIVERY인 데이터
        Page<Delivery> deliveries = deliveryRepository.findByOrderingInAndDeliveryStatusAndDeliveryDueDateBefore(
                orderingList, DeliveryStatus.BEFORE_DELIVERY, LocalDate.now().plusDays(4), pageable);

        return deliveries.map(delivery -> {
            MemberDetailResponse member = memberFeign.getMemberById(delivery.getOrdering().getMemberId());
            PackageProductResponse packageProduct = productFeign.getPackageProduct(delivery.getOrdering().getPackageId());
            return DeliveryUpComingListResponse.from(delivery, member.name(), packageProduct.packageName());
        });
    }

    /**
     * 사장님 페이지 > 운송장 번호를 등록하면 delivery 엔티티의 status가 IN_DELIVERY로 변경 + 현재 시각을 delivery_at에 추가
     */
    @Transactional
    public void createBillingNumber(Long id, BillingNumberCreateRequest dto) {

        Delivery delivery = deliveryRepository.findByIdOrThrow(id);
        LocalDateTime now = LocalDateTime.now();

        // delivery의 운송장 번호 추가 및 상태 변경
        if (dto.billingNumber() != null) {
            delivery.updateDelivery(dto.billingNumber(), DeliveryStatus.IN_DELIVERY, now);
        }
        deliveryRepository.save(delivery);

        // 알림 전송
        issueDeliveryNotificationToMember(delivery);
    }

    private void issueDeliveryNotificationToMember(Delivery delivery) {
        Long memberId = delivery.getOrdering().getMemberId();
        PackageProductResponse packageProduct = productFeign.getPackageProduct(delivery.getOrdering().getPackageId());
        KafkaNotificationRequest notification = KafkaNotificationRequest.builder()
                .memberId(memberId)
                .title("상품 배송이 시작됐어요!")
                .content("상품명: " + packageProduct.packageName())
                .build();

        kafkaTemplate.send("send-notification-topic", notification);
    }

    /**
     * 사장님 페이지 > 처리한 배송 조회
     */
    public Page<DeliveryCompletedListResponse> completedDelivery(Long sellerId, Pageable pageable) {
        // 사장이 내농장 찾아옴
        FarmDetailGetResponse farm = productFeign.getFarmInfoWithSeller(sellerId);
        List<Ordering> orderingList = orderRepository.findByFarmId(farm.id());

        // IN_DELIVERY, AFTER_DELIVERY인 배송건 가져오기
        List<DeliveryStatus> statuses = List.of(DeliveryStatus.IN_DELIVERY, DeliveryStatus.AFTER_DELIVERY);
        Page<Delivery> deliveries = deliveryRepository.findByOrderingInAndDeliveryStatusIn(orderingList, statuses, pageable);

        return deliveries.map(delivery -> {
            MemberDetailResponse member = memberFeign.getMemberById(delivery.getOrdering().getMemberId());
            PackageProductResponse packageProduct = productFeign.getPackageProduct(delivery.getOrdering().getPackageId());

            return DeliveryCompletedListResponse.from(delivery, member.name(), packageProduct.packageName());
        });
    }

    public Page<DeliveryListResponse> getOrderDeliveryList(Pageable pageable, Long memberId) {
        List<Ordering> orderlist = orderRepository.findAllByMemberId(memberId);
        List<DeliveryStatus> statuses = List.of(DeliveryStatus.IN_DELIVERY, DeliveryStatus.AFTER_DELIVERY);
        Page<Delivery> deliveries = deliveryRepository.findByOrderingInAndDeliveryStatusIn(orderlist, statuses, pageable);

        return deliveries.map(delivery -> {
            PackageProductResponse product = productFeign.getPackageProduct(delivery.getOrdering().getPackageId());
            return DeliveryListResponse.fromEntity(product, delivery);
        });
    }
}
