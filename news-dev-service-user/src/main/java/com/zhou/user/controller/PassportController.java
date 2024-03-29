package com.zhou.user.controller;

import com.zhou.api.BaseController;
import com.zhou.api.controller.user.PassportControllerApi;
import com.zhou.enums.UserStatus;
import com.zhou.grace.result.GraceJSONResult;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.pojo.AppUser;
import com.zhou.pojo.bo.RegistLoginBO;
import com.zhou.user.service.UserService;
import com.zhou.utils.IPUtil;
import com.zhou.utils.JsonUtils;
import com.zhou.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;


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

    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {

        // 获取用户 ip
        String userIp = IPUtil.getRequestIp(request);

        // 根据用户 ip 进行限制, 限制用户在60秒内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        // 生成随机验证码, 并且发送短信
        String random = (int) ((Math.random() * 9 + 1) * 10000) + "";    //5位整数
        smsUtils.sendSMS("15838786860", random);

        // 把验证码存入 redis, 用于后续验证
        redis.set(MOBILE_SMSCODE + ":" + mobile, random, 30 * 60);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(@Valid RegistLoginBO registLoginBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

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

        // 2. 查询数据库, 判断该用户是否注册
        AppUser user = userService.queryMobileIsExist(mobile);
        if (user != null && user.getActiveStatus() == UserStatus.FROZEN.type) {
            // 如果用户不为空, 并且状态为冻结, 则直接抛出异常, 禁止登录
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (user == null) {
            // 如果用户没有注册过, 则为null, 需要注册信息入库
            user = userService.createUser(mobile);
        }

        // 3. 保存用户分布式会话的相关操作
        int userActiveStatus = user.getActiveStatus();
        if (userActiveStatus != UserStatus.FROZEN.type) {
            // 保存 token 到 redis
            String uToken = UUID.randomUUID().toString();
            redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);
            redis.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user));

            // 保存用户 id 和 token 到 cookie 中
            setCookie(request, response, "utoken", uToken, COOKIE_MONTH);
            setCookie(request, response, "uid", user.getId(), COOKIE_MONTH);
        }

        //  4. 用户登录或注册后, 需要删除 redis 中的短信验证码, 验证码只能使用一次, 用过后则作废
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        // 5. 返回用户状态
        return GraceJSONResult.ok(userActiveStatus);
    }

    @Override
    public GraceJSONResult doLogin(String userId, HttpServletRequest request, HttpServletResponse response) {

        redis.del(REDIS_USER_TOKEN + ":" + userId);

        setCookie(request,response,"utoken","",COOKIE_DELETE);
        setCookie(request,response,"uid","", COOKIE_DELETE);

        return null;
    }
}
