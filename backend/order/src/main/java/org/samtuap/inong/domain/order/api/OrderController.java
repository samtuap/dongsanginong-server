package org.samtuap.inong.domain.order.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.samtuap.inong.domain.order.dto.OrderDeliveryListResponse;
import org.samtuap.inong.domain.order.dto.OrderPaymentListResponse;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.service.OrderService;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/order")
@RestController
public class OrderController {
    private final OrderService orderService;

    // feign 요청 용
    @GetMapping("/package/top")
    public List<Long> getTopPackages() {
        return orderService.getTopPackages();
    }

    // feign 요청용
    @PostMapping("/counts")
    public Long getAllOrders(@RequestBody Long packageId){
        return orderService.getAllOrders(packageId);
    }

    @PostMapping("/first")
    public ResponseEntity<PaymentResponse> kakaoPay(@RequestHeader("myId") Long memberId,
                                                    @RequestBody PaymentRequest reqDto) {
        PaymentResponse paymentResponse = orderService.makeFirstOrder(memberId, reqDto);

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

    @GetMapping("/delivery/list")
    public ResponseEntity<List<OrderDeliveryListResponse>> getOrderDeliveryList(@RequestHeader("myId") Long memberId){
        List<OrderDeliveryListResponse> myOrderDeliveryList = orderService.getOrderDeliveryList(memberId);
        return new ResponseEntity<>(myOrderDeliveryList, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<OrderPaymentListResponse>> getOrderPaymentList(@PageableDefault(size = 8) Pageable pageable, @RequestHeader("myId") Long memberId){
        Page<OrderPaymentListResponse> myOrderPaymentList = orderService.getOrderPaymentList(pageable, memberId);
        return new ResponseEntity<>(myOrderPaymentList, HttpStatus.OK);
    }

}
