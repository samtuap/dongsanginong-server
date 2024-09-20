package org.samtuap.inong.common.exception;

import org.samtuap.inong.common.exceptionType.ExceptionType;

public class TokenException extends BaseCustomException {
    public TokenException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
