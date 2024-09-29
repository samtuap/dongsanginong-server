package org.samtuap.inong.domain.order.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.SalesDataGetResponse;
import org.samtuap.inong.domain.order.dto.SalesElementGetResponse;
import org.samtuap.inong.domain.order.dto.SalesTableGetRequest;
import org.samtuap.inong.domain.order.service.OrderBackOfficeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 사장님 페이지 > 매출
@RequiredArgsConstructor
@RequestMapping("/farm/backoffice")
@RestController
public class OrderBackOfficeController {
    private final OrderBackOfficeService orderBackOfficeService;

    @PostMapping("/sales-data")
    public ResponseEntity<SalesDataGetResponse> getSalesNumberData(@PageableDefault(size = 20) Pageable pageable,
                                                                   @RequestBody SalesTableGetRequest request,
                                                                   @RequestHeader("sellerId") Long sellerId) {
        SalesDataGetResponse salesData = orderBackOfficeService.getSalesData(request, sellerId);
        return new ResponseEntity<>(salesData, HttpStatus.OK);
    }


    @PostMapping("/sales-list")
    public ResponseEntity<Page<SalesElementGetResponse>> getSalesList(@PageableDefault(size = 20) Pageable pageable,
                                                                      @RequestBody SalesTableGetRequest request,
                                                                      @RequestHeader("sellerId") Long sellerId) {
        Page<SalesElementGetResponse> salesList = orderBackOfficeService.getSalesList(pageable, request, sellerId);
        return new ResponseEntity<>(salesList, HttpStatus.OK);
    }
}
