package org.samtuap.inong.domain.product.api;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.PRODUCT_NOT_FOUND;


@RequestMapping("/product")
@RestController
public class ProductController {
    @GetMapping
    public String testApi() {
        return "product-test!!";
    }

    @GetMapping("/test")
    public void exceptionTest() {
        throw new BaseCustomException(PRODUCT_NOT_FOUND);
    }
}
