package org.samtuap.inong.domain.farm.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farm.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.farm.dto.FarmListGetResponse;
import org.samtuap.inong.domain.farm.service.FarmService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/farm")
@RequiredArgsConstructor
@RestController
public class FarmController {
    private final FarmService farmService;

    @GetMapping("/list")
    public ResponseEntity<Page<FarmListGetResponse>> getFarmList(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FarmListGetResponse> farmList = farmService.getFarmList(pageable);
        return new ResponseEntity<>(farmList, HttpStatus.OK);
    }

    @GetMapping("/detail/{farmId}")
    public ResponseEntity<FarmDetailGetResponse> getFarmDetail(@PathVariable Long farmId) {
        FarmDetailGetResponse farmDetail = farmService.getFarmDetail(farmId);
        return new ResponseEntity<>(farmDetail, HttpStatus.OK);
    }
}
