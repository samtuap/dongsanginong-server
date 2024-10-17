package org.samtuap.inong.common.exceptionType;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReceiptExceptionType implements ExceptionType {
    RECEIPT_NOT_FOUND(HttpStatus.NOT_FOUND, "영수증이 존재하지 않습니다.");
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

