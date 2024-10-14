package org.samtuap.inong.domain.order.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.service.OrderTestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 동기(feign), 비동기(kafka) 성능 비교를 위해 만든 API
@RequiredArgsConstructor
@RequestMapping("/test/order")
@RestController
public class OrderTestController {
    private final OrderTestService orderTestService;
    @PostMapping
    public ResponseEntity<PaymentResponse> kakaoPay(@RequestHeader("myId") Long memberId,
                                                    @RequestBody PaymentRequest reqDto) {
        PaymentResponse paymentResponse = orderTestService.makeFirstOrder(memberId, reqDto);

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}
