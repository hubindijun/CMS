package com.cms.common.exception;

import lombok.Getter;

/**
 * 业务异常，由业务逻辑主动抛出
 */
@Getter
public class BusinessException extends RuntimeException {
    /** 错误码 */
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
