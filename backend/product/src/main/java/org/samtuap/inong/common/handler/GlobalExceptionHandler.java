package org.samtuap.inong.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exceptionType.ExceptionType;
import org.samtuap.inong.common.response.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("[Validation Error] {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomErrorResponse.builder()
                        .name(HttpStatus.BAD_REQUEST.name())
                        .httpStatusCode(HttpStatus.BAD_REQUEST.value())
                        .message("요청 데이터가 유효하지 않습니다.")
                        .build());
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
