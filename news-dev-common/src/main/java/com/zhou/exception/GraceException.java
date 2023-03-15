package com.zhou.exception;

import com.zhou.grace.result.ResponseStatusEnum;

/**
 * @Description: 优雅地处理异常, 统一封装
 * @Author: yuhang
 * @Date: 2023/3/15 16:32
 * @Version: 1.0
 **/
public class GraceException {

    public static void display(ResponseStatusEnum responseStatusEnum) {
        throw new MyCustomException(responseStatusEnum);
    }

}
