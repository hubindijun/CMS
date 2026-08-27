package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {
    /** 响应码 */
    private Integer code;
    /** 响应消息 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        return r;
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    /**
     * 失败响应（默认 500 错误码）
     *
     * @param message 错误消息
     */
    public static <T> Result<T> fail(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    /**
     * 失败响应（自定义错误码）
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
