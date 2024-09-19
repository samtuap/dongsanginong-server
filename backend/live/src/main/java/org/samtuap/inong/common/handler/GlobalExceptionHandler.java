package org.samtuap.inong.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.ExceptionType;
import org.samtuap.inong.common.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<CustomErrorResponse> handleBaseException(BaseCustomException e) {
        ExceptionType exceptionType = e.getExceptionType();
        e.printStackTrace();
        return ResponseEntity.status(exceptionType.httpStatus())
                .body(CustomErrorResponse.of(exceptionType));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception e) {
        log.error("[Unhandled Error] {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomErrorResponse.builder()
                        .name(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("서버 에러입니다.")
                        .build());
    }
}
