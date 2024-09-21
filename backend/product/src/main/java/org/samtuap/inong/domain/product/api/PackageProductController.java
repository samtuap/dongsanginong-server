package org.samtuap.inong.domain.product.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.product.dto.PackageProductResponse;
import org.samtuap.inong.domain.product.dto.TopPackageGetResponse;
import org.samtuap.inong.domain.product.service.PackageProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/top10")
    public List<TopPackageGetResponse> getTopPackages() {
        return packageProductService.getTopPackages();
    }

    // Member 의 MyPage 에서 Product 를 조회하기 위한 메서드
    @GetMapping("/info")
    public PackageProductResponse getPackageProduct(@RequestParam("id") Long packageProductId) {
        return packageProductService.getProductInfo(packageProductId);
    }
}
