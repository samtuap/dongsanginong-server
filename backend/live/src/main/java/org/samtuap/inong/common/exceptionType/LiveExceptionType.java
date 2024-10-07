package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum LiveExceptionType implements ExceptionType {
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 session이 존재하지 않습니다."),
    LIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 live가 존재하지 않습니다.");

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
