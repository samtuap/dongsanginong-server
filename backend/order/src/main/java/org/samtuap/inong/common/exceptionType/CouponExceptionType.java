package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CouponExceptionType  implements ExceptionType{
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 조회할 수 없습니다."),
    CANNOT_APPLY_COUPON(HttpStatus.BAD_REQUEST, "쿠폰을 적용할 수 없는 상품입니다."),
    COUPON_NOT_ISSUED(HttpStatus.BAD_REQUEST, "쿠폰 발급 이력이 없습니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 배송건이 존재하지 않습니다."),
    ALREADY_DOWNLOADED_COUPON(HttpStatus.BAD_REQUEST, "이미 발급된 쿠폰입니다."),
    FARM_NOT_FOUND(HttpStatus.FORBIDDEN, "쿠폰 생성할 권한이 없습니다."),
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
