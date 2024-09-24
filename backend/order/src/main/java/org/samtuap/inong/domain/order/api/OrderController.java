package org.samtuap.inong.domain.order.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.PaymentRequest;
import org.samtuap.inong.domain.order.dto.PaymentResponse;
import org.samtuap.inong.domain.order.dto.TopPackageResponse;
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


    // feign 요청 용 (member > subscribe 할 때 요청)
    @PostMapping("/first")
    public ResponseEntity<PaymentResponse> kakaoPay(@RequestHeader("myId") Long memberId,
                                                    @RequestBody PaymentRequest reqDto) {
        PaymentResponse paymentResponse = orderService.makeOrder(memberId, reqDto);

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

}
