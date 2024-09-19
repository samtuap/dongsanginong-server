package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TokenExceptionType implements ExceptionType {

    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "token 이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token 이 유효하지 않습니다."),
    NO_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token 이 존재하지 않습니다.");



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
