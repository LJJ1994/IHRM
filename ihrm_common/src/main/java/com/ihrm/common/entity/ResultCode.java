package com.ihrm.common.entity;

public enum ResultCode {
    SUCCESS(true, 10000, "操作成功"),
    FAIL(false, 10001, "操作成功"),
    UNAUTHENCATED(false, 10002, "未认证"),
    UNAUTHORISE(false, 10003, "未授权"),
    SERVER_ERROR(false, 10004, "服务器错误!"),
    LOGIN_SUCCESS(false, 10005, "登录成功"),
    LOGIN_ERROR(false, 10006, "用户名或者密码错误");

    private boolean success = true;
    private Integer code = 10000;
    private String message = "操作成功";

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    private ResultCode(){}

    public Integer code(){
        return code;
    }

    public String message(){
        return message;
    }

    public boolean success(){
        return success;
    }
}
