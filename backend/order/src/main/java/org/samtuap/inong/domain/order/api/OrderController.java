package org.samtuap.inong.domain.order.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.dto.TopPackageResponse;
import org.samtuap.inong.domain.order.service.OrderService;
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


    @PostMapping("/payment/kakao")
    public ResponseEntity<PaymentResponse> kakaoPay(@RequestHeader("myId") Long memberId,
                                                    @RequestBody PaymentRequest reqDto) {
        PaymentResponse paymentResponse = orderService.makeOrder(memberId, reqDto);
    }
}
