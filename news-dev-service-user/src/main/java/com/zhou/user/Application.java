package com.zhou.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zyh
 * @create 2023-02-03 16:11
 */
@SpringBootApplication
@MapperScan(basePackages = "com.zhou.user.mapper")
@ComponentScan(basePackages = {"com.zhou", "org.n3r.idworker"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
