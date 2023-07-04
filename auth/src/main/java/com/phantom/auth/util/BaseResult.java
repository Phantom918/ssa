package com.phantom.auth.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础消息对象
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/6/30 14:14
 */
@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 信息
     */
    private String message;

    /**
     * 数据体
     */
    private T data;


    public BaseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResult(StatusEnum statusEnum, T data) {
        this.code = statusEnum.getValue();
        this.message = statusEnum.getDescription();
        this.data = data;
    }


    public static BaseResult<String> success() {
        return new BaseResult<>(StatusEnum.SUCCESS.getValue(), StatusEnum.SUCCESS.getDescription());
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<T>(StatusEnum.SUCCESS, data);
    }

    public static BaseResult<String> error() {
        return new BaseResult<>(StatusEnum.ERROR.getValue(), StatusEnum.ERROR.getDescription());
    }


}
