package com.zhou.user.controller;

import com.zhou.api.controller.user.HelloControllerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zyh
 * @create 2023-02-03 15:43
 */
@RestController
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    public Object hello(){

        logger.debug("debug: hello~");
        logger.info("info: hello~");
        logger.warn("warn: hello~");
        logger.error("error: hello~");

        return "hello";
    }
}
