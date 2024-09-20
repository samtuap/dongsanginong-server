package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemberExceptionType implements ExceptionType {

    INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 방식의 로그인입니다."),
    NEED_TO_REGISTER(HttpStatus.BAD_REQUEST, "회원가입이 필요합니다."),
    NOT_A_NEW_MEMBER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    FAIL_TO_AUTH(HttpStatus.BAD_REQUEST, "인증에 실패했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 id의 회원이 존재하지 않습니다.");

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
