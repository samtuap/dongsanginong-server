package org.samtuap.inong.domain.farm.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farm.dto.*;
import org.samtuap.inong.domain.farm.service.FarmService;
import org.samtuap.inong.domain.seller.dto.FarmCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/farm")
@RequiredArgsConstructor
@RestController
public class FarmController {
    private final FarmService farmService;

    @GetMapping("/no-auth/list")
    public ResponseEntity<Page<FarmListGetResponse>> getFarmList(
            @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FarmListGetResponse> farmList = farmService.getFarmList(pageable);
        return new ResponseEntity<>(farmList, HttpStatus.OK);
    }

    @GetMapping("/no-auth/search")
    public ResponseEntity<Page<FarmListGetResponse>> searchFarm(@RequestParam String farmName,
                                                                @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FarmListGetResponse> farmList = farmService.farmSearch(farmName, pageable);
        return new ResponseEntity<>(farmList, HttpStatus.OK);
    }

    @GetMapping("/no-auth/detail/{farmId}")
    public ResponseEntity<FarmDetailGetResponse> getFarmDetail(@PathVariable Long farmId) {
        FarmDetailGetResponse farmDetail = farmService.getFarmDetail(farmId);
        return new ResponseEntity<>(farmDetail, HttpStatus.OK);
    }


    // Member -> Feign 요청용 메서드
    @PostMapping("/favorite/list")
    public List<FarmFavoriteResponse> getFarmFavoriteList(@RequestBody List<Long> farmFavoriteIds) {
        return farmService.getFarmFavoriteList(farmFavoriteIds);
    }

    /**
     * feign 요청용
     */
    @GetMapping("/{id}")
    public FarmDetailGetResponse findMember(@PathVariable("id") Long farmId) {
        return farmService.getFarmDetail(farmId);
    }

    /**
     * feign 요청용
     */
    @PostMapping("/favorites/list")
    public List<FavoritesLiveListResponse> getFavoritesFarmLiveList(@RequestBody List<Long> favoriteFarmList) {
        return farmService.getFavoritesFarmLiveList(favoriteFarmList);


    }

    /**
     * feign 요청용 (sellerId로 입력받음)
     * sellerId에 해당하는 사장님의 '농장' 정보 반환
     */
    @GetMapping("/seller/{sellerId}")
    public FarmDetailGetResponse getFarmInfoWithSeller(@PathVariable("sellerId") Long sellerId) {
        return farmService.getFarmInfoWithSeller(sellerId);
    }


    /**
     * feign 요청용 (farmId로 입력받음)
     */
    @GetMapping("/seller-by-farm/{farmId}")
    public FarmSellerResponse getSellerIdByFarm(@PathVariable("farmId") Long farmId) {
        return farmService.getSellerIdByFarm(farmId);
    }

    @PostMapping("/create")
    public ResponseEntity<FarmCreateResponse> createFarm(@RequestBody FarmCreateRequest request,
                                         @RequestHeader("sellerId") Long sellerId) {
        FarmCreateResponse response = farmService.createFarm(request, sellerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<FarmCategoryResponse>> getAllCategories() {
        List<FarmCategoryResponse> categories = farmService.getAllFarmCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
