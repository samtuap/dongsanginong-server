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
    public ResponseEntity<SalesDataGetResponse> getSalesNumberData(@RequestBody SalesTableGetRequest request,
                                                                   @RequestHeader("sellerId") Long sellerId) {
        SalesDataGetResponse salesData = orderBackOfficeService.getSalesData(request, sellerId);
        return new ResponseEntity<>(salesData, HttpStatus.OK);
    }


    @PostMapping("/sales-list")
    public ResponseEntity<List<SalesElementGetResponse>> getSalesList(@RequestBody SalesTableGetRequest request,
                                                                      @RequestHeader("sellerId") Long sellerId) {
        List<SalesElementGetResponse> salesList = orderBackOfficeService.getSalesList(request, sellerId);
        return new ResponseEntity<>(salesList, HttpStatus.OK);
    }
}
