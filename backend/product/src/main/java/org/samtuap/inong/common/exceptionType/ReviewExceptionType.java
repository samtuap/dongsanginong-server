package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReviewExceptionType implements ExceptionType {
    REIEW_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "이 상품에 대한 리뷰는 이미 존재합니다.");


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
