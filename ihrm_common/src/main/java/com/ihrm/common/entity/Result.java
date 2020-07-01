package com.ihrm.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 非空数据不显示
public class Result<T> {
    private String message;
    private Integer code;
    private boolean success;
    private T data;

    public Result(){

    }

    public Result(ResultCode code){
        this.code = code.code();
        this.message = code.message();
        this.success = code.success();
    }

    public Result(ResultCode code, T data){
        this.code = code.code();
        this.message = code.message();
        this.success = code.success();
        this.data = data;
    }

    public Result(boolean success, Integer code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Result(boolean success, Integer code, String message, T data){
        this.success = success;
        this.code = code;
        this.message = message;
        this.data =data;
    }

    public static Result SUCCESS(){
        return new Result(ResultCode.SUCCESS);
    }

    public static Result FAIL(){
        return new Result(ResultCode.FAIL);
    }

    public static Result ERROR(){
        return new Result(ResultCode.SERVER_ERROR);
    }
}
