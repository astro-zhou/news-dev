package com.zhou.user.controller;

import com.zhou.api.BaseController;
import com.zhou.api.controller.user.UserControllerApi;
import com.zhou.grace.result.GraceJSONResult;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.pojo.AppUser;
import com.zhou.pojo.bo.UpdateUserInfoBO;
import com.zhou.pojo.vo.UserAccountInfoVO;
import com.zhou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;


/**
 * @author zyh
 * @create 2023-02-03 15:43
 */
@RestController
//@RequestMapping(method = {RequestMethod.GET,RequestMethod.POST})
public class UserController extends BaseController implements UserControllerApi {

    final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

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
        // TODO 本方法后续公用, 并且扩展
        AppUser user = userService.getUser(userId);
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
        return GraceJSONResult.ok();
    }

}
