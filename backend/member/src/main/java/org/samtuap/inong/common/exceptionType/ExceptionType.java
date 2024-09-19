package org.samtuap.inong.common.exceptionType;

import org.springframework.http.HttpStatus;

public interface ExceptionType {
    String name();

    HttpStatus httpStatus();

    String message();
}