package com.zhou.user.controller;

import com.zhou.api.BaseController;
import com.zhou.api.controller.user.UserControllerApi;
import com.zhou.grace.result.GraceJSONResult;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.pojo.AppUser;
import com.zhou.pojo.bo.UpdateUserInfoBO;
import com.zhou.pojo.vo.AppUserVO;
import com.zhou.pojo.vo.UserAccountInfoVO;
import com.zhou.user.service.UserService;
import com.zhou.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;


/**
 * @author zyh
 * @create 2023-02-03 15:43
 */
@RestController
public class UserController extends BaseController implements UserControllerApi {

    final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Override
    public GraceJSONResult getUserInfo(String userId) {

        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        // 1. 根据 userId 查询用户信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);

        return GraceJSONResult.ok(userVO);
    }

    @Override
    public GraceJSONResult getAccountInfo(String userId) {

        // 0. 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        // 1. 根据 userId 查询用户信息
        AppUser user = getUser(userId);

        // 2. 返回用户信息
        UserAccountInfoVO accountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, accountInfoVO);

        return GraceJSONResult.ok(accountInfoVO);

    }


    private AppUser getUser(String userId) {

        // 查询判断 redis 中是否包含用户信息, 若包含, 则查询后直接返回, 就不去查询数据库了
        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);
        AppUser user = null;
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        } else {
            user = userService.getUser(userId);
            // 由于用户信息不怎么会变动, 对于一些千万级别的网站来说, 这类信息不会直接去查数据库
            // 完全可以依靠 redis, 直接把查询后的数据存入到 redis 中
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }
        return user;
    }

    @Override
    public GraceJSONResult updateUserInfo(@Valid UpdateUserInfoBO updateUserInfoBO,
                                          BindingResult result) {

        // 0. 校验 BO
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return GraceJSONResult.errorMap(map);
        }

        // 1. 执行更新操作
        userService.updateUserInfo(updateUserInfoBO);

        return GraceJSONResult.ok();
    }

}
