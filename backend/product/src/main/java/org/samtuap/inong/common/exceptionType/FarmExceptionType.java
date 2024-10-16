package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FarmExceptionType implements ExceptionType {
    FARM_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 입니다."),
    FARM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 농장이 존재합니다."),
    INVALID_ORDER_COUNT_REQUEST(HttpStatus.BAD_REQUEST, "주문 수 변경 요청이 올바르지 않습니다.");

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