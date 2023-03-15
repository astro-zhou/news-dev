package com.zhou.user.controller;

import com.zhou.api.BaseController;
import com.zhou.api.controller.user.PassportControllerApi;
import com.zhou.grace.result.GraceJSONResult;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.pojo.bo.RegistLoginBO;
import com.zhou.utils.IPUtil;
import com.zhou.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author zyh
 * @create 2023-02-03 15:43
 */
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController implements PassportControllerApi {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private SMSUtils smsUtils;

    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {

        // 获取用户 ip
        String userIp = IPUtil.getRequestIp(request);

        // 根据用户 ip 进行限制, 限制用户在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        // 生成随机验证码, 并且发送短信
        String random = String.valueOf((Math.random() * 9 + 1) * 10000);    //5位数
        smsUtils.sendSMS("15838786860", random);

        // 把验证码存入 redis, 用于后续验证
        redis.set(MOBILE_SMSCODE + ":" + mobile, random, 30 * 60);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(@Valid RegistLoginBO registLoginBO, BindingResult result) {

        // 0. 判断 BindingResult 中是否保存了错误的验证信息, 如果有, 则需要返回
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return GraceJSONResult.errorMap(map);
        }

        // 1. 校验验证码是否匹配
        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();

        String redisSmsCode = redis.get(MOBILE_SMSCODE + ":" + mobile);

        if (StringUtils.isBlank(redisSmsCode) || !redisSmsCode.equalsIgnoreCase(smsCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        return GraceJSONResult.ok();
    }
}
