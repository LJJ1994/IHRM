package com.ihrm.common.exception;

import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(HttpServletRequest request, HttpServletResponse response, Exception e){
        e.printStackTrace();
        if (e.getClass() == CommonException.class){
            CommonException ec = (CommonException) e;
            return new Result(ec.getCode());
        }else{
            return new Result(ResultCode.SERVER_ERROR);
        }
    }
}
