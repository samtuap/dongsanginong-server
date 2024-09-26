package org.samtuap.inong.domain.order.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.dto.SalesTableGetRequest;
import org.samtuap.inong.domain.order.service.OrderBackOfficeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 사장님 페이지 > 매출
@RequiredArgsConstructor
@RequestMapping("/farm/backoffice")
@RestController
public class OrderBackOfficeController {
    private final OrderBackOfficeService orderBackOfficeService;

    @PostMapping("/sales-data")
    public ResponseEntity<SalesDataGetResponse> getSalesData(@RequestBody SalesTableGetRequest request,
                                                             @RequestHeader("sellerId") Long sellerId) {
        SalesDataGetResponse salesData = orderBackOfficeService.getSalesData(request, sellerId);
        return new ResponseEntity<>(salesData, HttpStatus.OK);
    }


    @PostMapping("/sales-list")
    public ResponseEntity<SalesDataGetResponse> getSalesData(@RequestBody SalesTableGetRequest request,
                                                             @RequestHeader("sellerId") Long sellerId) {
        SalesDataGetResponse salesData = orderBackOfficeService.getSalesList(request, sellerId);
        return new ResponseEntity<>(salesData, HttpStatus.OK);
    }
}
