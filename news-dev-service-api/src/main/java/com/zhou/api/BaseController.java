package com.zhou.api;

import com.zhou.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;


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
}
