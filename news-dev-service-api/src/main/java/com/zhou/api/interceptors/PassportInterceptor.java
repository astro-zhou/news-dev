package com.zhou.api.interceptors;

import com.zhou.exception.GraceException;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.utils.IPUtil;
import com.zhou.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: yuhang
 * @Date: 2023/3/13 19:13
 * @Version: 1.0
 **/
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    /**
     * 拦截请求，访问controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);

        boolean keyIsExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp);

        if (keyIsExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
//            System.out.println("短信发送频率太大!");
            return false;
        }

        /**
         * false：请求被拦截
         * true：请求通过验证，放行
         */
        return true;
    }


    /**
     * 请求访问到controller之后，渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问到controller之后，渲染视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}