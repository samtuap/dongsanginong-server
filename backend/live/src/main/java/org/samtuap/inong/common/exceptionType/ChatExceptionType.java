package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatExceptionType implements ExceptionType {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 회원이 존재하지 않습니다."),
    AUTHORIZATION_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없거나 유효하지 않습니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    FARM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당의 아이디 농장이이 존재하지 않습니다."),
    IS_NOT_OWNER(HttpStatus.UNAUTHORIZED, "해당 사용자는 owner가 아닙니다."),
    ID_IS_NULL(HttpStatus.BAD_REQUEST, "id가 null 입니다.");


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
