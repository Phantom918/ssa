package com.phantom.auth.util;

/**
 * 状态码枚举类
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/6/30 14:18
 */
public enum StatusEnum {

    ERROR(-1, "操作失败！"),
    SUCCESS(0, "操作成功！"),
    HTTP_OK(200, "请求成功！"),
    HTTP_NOT_FOUND(400, "请求成功！"),
    HTTP_NOT_LOGIN(401, "未登录！"),
    HTTP_NOT_PERMISSION(403, "无权访问！"),
    HTTP_ERROR(500, "服务处理异常！");


    /**
     * 状态码
     */
    private final int value;

    /**
     * 装填描述
     */
    private final String description;

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    StatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    private static final StatusEnum[] VALUES;

    static {
        VALUES = values();
    }


    /**
     * 通过 code 查询对应的枚举对象
     *
     * @param statusCode 状态码
     * @return 枚举对象
     */
    public static StatusEnum resolve(int statusCode) {
        for (StatusEnum status : VALUES) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    /**
     * 通过 code 查询对应的枚举对象
     *
     * @param statusCode 状态码
     * @return 状态描述
     */
    public static String resolveMessage(int statusCode) {
        for (StatusEnum status : VALUES) {
            if (status.value == statusCode) {
                return status.description;
            }
        }
        return null;
    }


}
