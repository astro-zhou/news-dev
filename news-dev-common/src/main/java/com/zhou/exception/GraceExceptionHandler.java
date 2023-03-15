package com.zhou.exception;

import com.zhou.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 统一异常拦截处理
 * 可以针对异常的类型进行捕获, 然后返回 json 信息到前端
 * @Author: yuhang
 * @Date: 2023/3/15 16:45
 * @Version: 1.0
 **/
@ControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyException(MyCustomException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

}
