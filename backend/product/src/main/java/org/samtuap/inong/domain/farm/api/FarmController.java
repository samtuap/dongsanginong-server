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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public ResponseEntity<Page<FarmListGetResponse>> searchFarm(@RequestParam String farmName,
                                                                @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FarmListGetResponse> farmList = farmService.farmSearch(farmName, pageable);
        return new ResponseEntity<>(farmList, HttpStatus.OK);
    }

    @GetMapping("/detail/{farmId}")
    public ResponseEntity<FarmDetailGetResponse> getFarmDetail(@PathVariable Long farmId) {
        FarmDetailGetResponse farmDetail = farmService.getFarmDetail(farmId);
        return new ResponseEntity<>(farmDetail, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public FarmDetailGetResponse findMember(@PathVariable("id") Long farmId) {
        return farmService.getFarmDetail(farmId);
    }
}
