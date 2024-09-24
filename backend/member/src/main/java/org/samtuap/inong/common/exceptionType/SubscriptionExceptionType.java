package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SubscriptionExceptionType implements ExceptionType {

    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "구독 정보가 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "구독을 수정/삭제할 권한이 없습니다.");

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
