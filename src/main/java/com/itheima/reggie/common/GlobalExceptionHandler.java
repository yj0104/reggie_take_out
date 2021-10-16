package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author amass_
 * @date 2021/10/16
 * 全局错误捕获处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param ex:将错误信息存进此变量
     * @return
     *
     * @ExceptionHandler:处理参数列表里设置的错误类型
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String>  exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //将错误信息输出到控制台
        log.error(ex.getMessage());
        //判断错误类型,将详细信息返回给前端
        if (ex.getMessage().contains("Duplicate entry")){
            //String的split方法:根据参数列表提供的字符进行字符串切割
            //ex.getMessage():提供错误信息
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
}
