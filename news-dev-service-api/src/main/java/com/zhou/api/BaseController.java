package com.zhou.api;

import com.zhou.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description:
 * @Author: yuhang
 * @Date: 2023/3/11 17:30
 * @Version: 1.0
 **/
public class BaseController {

    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    /**
     * 获取 BO 中的错误信息
     *
     * @param result
     */
    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            // 发送验证码错误时所对应的某个属性
            String field = error.getField();
            // 验证的错误信息
            String msg = error.getDefaultMessage();

            map.put(field, msg);
        }
        return map;
    }
}
