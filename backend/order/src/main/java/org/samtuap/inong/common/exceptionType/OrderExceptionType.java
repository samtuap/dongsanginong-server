package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OrderExceptionType  implements ExceptionType{
    FAIL_TO_PAY(HttpStatus.INTERNAL_SERVER_ERROR, "결제 실패"),
    BILLING_KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "빌링키가 존재하지 않습니다."),
    INVALID_PACKAGE_PRODUCT(HttpStatus.BAD_REQUEST, "주문하려는 상품이 유효하지 않습니다."),
    INVALID_ROLLBACK_REQUEST(HttpStatus.BAD_REQUEST, "주문 롤백 요청 형식이 맞지 않습니다."),
    FAIL_TO_ROLLBACK_ORDER(HttpStatus.INTERNAL_SERVER_ERROR, "주문 취소에 실패했습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."),
    INVALID_PACKAGE_ID(HttpStatus.NOT_FOUND, "해당 아이디의 상품이 존재하지 않습니다."),
    INVALID_MEMBER_ID(HttpStatus.NOT_FOUND, "해당 아이디의 회원이 존재하지 않습니다.");
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
