package org.samtuap.inong.domain.product.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.product.dto.*;
import org.samtuap.inong.domain.product.service.PackageProductService;
import org.samtuap.inong.domain.product.dto.TopPackageGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.PRODUCT_NOT_FOUND;


@RequiredArgsConstructor
@RequestMapping("/product")
@RestController
public class PackageProductController {
    private final PackageProductService packageProductService;
    @GetMapping
    public String testApi() {
        return "product-test!!";
    }

    @GetMapping("/test")
    public void exceptionTest() {
        throw new BaseCustomException(PRODUCT_NOT_FOUND);
    }

    @GetMapping("/no-auth/top10")
    public List<TopPackageGetResponse> getTopPackages() {
        return packageProductService.getTopPackages();
    }

    //  Feign 요청용 메서드
    @GetMapping("/info/{id}")
    public PackageProductResponse getPackageProduct(@PathVariable("id") Long packageProductId) {
        return packageProductService.getProductInfo(packageProductId);
    }

    @PostMapping("/create")
    public ResponseEntity<PackageProductCreateResponse> createProduct(@RequestHeader Long sellerId,
                                                                      @RequestBody PackageProductCreateRequest request) {
        PackageProductCreateResponse packageProductCreateResponse = packageProductService.createPackageProduct(sellerId, request);
        return ResponseEntity.ok(packageProductCreateResponse);
    }

    //    Detail 메서드 (Feign이랑 url이 다름)
    @GetMapping("/no-auth/detail/{id}")
    public PackageProductResponse getPackageProductDetail(@PathVariable("id") Long packageProductId) {
        return packageProductService.getProductInfo(packageProductId);
    }

    // Feign 요청용 메서드
    @PostMapping("/info")
    List<PackageProductResponse> getPackageProductList(@RequestBody List<Long> ids) {
        return packageProductService.getProductInfoList(ids);
    }

    //  Feign 요청용 메서드
    @PostMapping("/subscription/list")
    public List<PackageProductSubsResponse> getProductSubsList(@RequestBody List<Long> subscriptionIds){
        return packageProductService.getProductSubsList(subscriptionIds);
    }

    // Feign 요청용 메서드
    @PostMapping("/info/contain-deleted")
    List<PackageProductResponse> getPackageProductListContainDeleted(@RequestBody List<Long> ids) {
        return packageProductService.getProductInfoListContainDeleted(ids);
    }

    @GetMapping("/no-auth/for-sale/{id}")
    public List<PackageProductForSaleListResponse> getForSalePackageProduct(@PathVariable("id") Long farmId) {
        return packageProductService.getForSalePackageProduct(farmId);
    }

    @GetMapping("/no-auth")
    public ResponseEntity<Page<AllPackageListResponse>> getAllPackageList(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(packageProductService.getAllPackageList(pageable));
    }

    @GetMapping("/no-auth/search")
    public ResponseEntity<Page<AllPackageListResponse>> searchProduct(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam("packageName") String packageName){
        return ResponseEntity.ok(packageProductService.searchProduct(pageable, packageName));
    }
}
