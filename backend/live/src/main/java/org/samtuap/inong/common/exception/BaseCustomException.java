package org.samtuap.inong.common.exception;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exceptionType.ExceptionType;

@RequiredArgsConstructor
@Getter
public class BaseCustomException extends RuntimeException {
    private final ExceptionType exceptionType;

    @Override
    public String getMessage() {
        return exceptionType.message();
    }
}