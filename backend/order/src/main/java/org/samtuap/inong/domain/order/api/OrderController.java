package org.samtuap.inong.domain.order.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.OrderDeliveryListResponse;
import org.samtuap.inong.domain.order.dto.OrderPaymentListResponse;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.service.OrderService;
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
    public ResponseEntity<List<OrderPaymentListResponse>> getOrderPaymentList(@RequestHeader("myId") Long memberId){
        List<OrderPaymentListResponse> myOrderPaymentList = orderService.getOrderPaymentList(memberId);
        return new ResponseEntity<>(myOrderPaymentList, HttpStatus.OK);
    }

}
