package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReviewExceptionType implements ExceptionType {
    REVIEW_ALREADY_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "이 상품에 대한 리뷰는 이미 존재합니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "이 상품에 대한 리뷰가 존재하지 않습니다."),
    AUTHORITY_NOT_FOUND(HttpStatus.FORBIDDEN, "이 상품에 대한 리뷰를 삭제 할 권한이 없습니다.");

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
