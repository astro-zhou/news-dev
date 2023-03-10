package com.zhou.user.controller;

import com.zhou.api.controller.user.HelloControllerApi;
import com.zhou.grace.result.GraceJSONResult;
import com.zhou.grace.result.JSONResult;
import com.zhou.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zyh
 * @create 2023-02-03 15:43
 */
@RestController
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private RedisOperator redis;

//    Swagger2 文档生成工具
    public Object hello(){

        logger.debug("debug: hello~");
        logger.info("info: hello~");
        logger.warn("warn: hello~");
        logger.error("error: hello~");

//        return "hello";
//        return JSONResult.ok();
//        return JSONResult.ok("hello");
//        return JSONResult.errorMsg("您的信息有误!");
        return GraceJSONResult.ok();
    }

    @GetMapping("/redis")
    public Object redis() {

        redis.set("age","18");

        return GraceJSONResult.ok(redis.get("age"));
    }
}
