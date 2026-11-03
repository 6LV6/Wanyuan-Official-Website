package com.sixlv6.backend.common;

import org.springframework.http.HttpStatus;

/**
 * 业务异常：由业务代码主动抛出，交给 GlobalExceptionHandler 统一转成 JSON。
 */
public class BusinessException extends RuntimeException {

    private final int code;
    private final HttpStatus status;

    public BusinessException(int code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public BusinessException(int code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
