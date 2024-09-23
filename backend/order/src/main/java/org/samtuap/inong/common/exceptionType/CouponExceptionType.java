package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CouponExceptionType  implements ExceptionType{
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 배송건이 존재하지 않습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 쿠폰이 존재하지 않습니다."),
    ALREADY_DOWNLOADED_COUPON(HttpStatus.BAD_REQUEST, "이미 발급된 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "해당 쿠폰은 유효기간이 만료되었습니다.");

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
