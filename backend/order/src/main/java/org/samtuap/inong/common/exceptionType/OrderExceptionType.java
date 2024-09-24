package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OrderExceptionType  implements ExceptionType{
    FAIL_TO_PAY(HttpStatus.INTERNAL_SERVER_ERROR, "결제 실패"),
    BILLING_KEY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "빌링키가 존재하지 않습니다."),
    INVALID_PACKAGE_PRODUCT(HttpStatus.BAD_REQUEST, "주문하려는 상품이 유효하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus httpStatus() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}
