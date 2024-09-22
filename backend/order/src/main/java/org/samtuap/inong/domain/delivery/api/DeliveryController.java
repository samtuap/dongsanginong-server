package org.samtuap.inong.domain.delivery.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.delivery.dto.BillingNumberCreateRequest;
import org.samtuap.inong.domain.delivery.dto.DeliveryUpComingListResponse;
import org.samtuap.inong.domain.delivery.entity.Delivery;
import org.samtuap.inong.domain.delivery.service.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * 사장님 페이지 > 다가오는 배송 처리
     */
    @GetMapping("/upcoming/list")
    public ResponseEntity<Page<DeliveryUpComingListResponse>> upcomingDelivery(
            @PageableDefault(size = 10, sort = "deliveryAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<DeliveryUpComingListResponse> list = deliveryService.upcomingDelivery(pageable);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * 사장님 페이지 > 운송장 번호를 등록하면 delivery 엔티티의 status가 IN_DELIVERY로 변경
     */
    @PatchMapping("/createBillingNumber/{id}")
    public ResponseEntity<?> createBillingNumber(@PathVariable("id") Long id,
                                                        @RequestBody BillingNumberCreateRequest dto) {
        deliveryService.createBillingNumber(id, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
