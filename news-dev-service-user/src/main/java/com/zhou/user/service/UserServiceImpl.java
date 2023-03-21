package com.zhou.user.service;

import com.zhou.enums.Sex;
import com.zhou.enums.UserStatus;
import com.zhou.exception.GraceException;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.pojo.AppUser;
import com.zhou.pojo.bo.UpdateUserInfoBO;
import com.zhou.user.mapper.AppUserMapper;
import com.zhou.utils.DateUtil;
import com.zhou.utils.DesensitizationUtil;
import com.zhou.utils.JsonUtils;
import com.zhou.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @Description:
 * @Author: yuhang
 * @Date: 2023/3/15 20:39
 * @Version: 1.0
 **/
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    public AppUserMapper appUserMapper;

    @Autowired
    public Sid sid;

    @Autowired
    private RedisOperator redis;

    public static final String REDIS_USER_INFO = "redis_user_info";

    private static final String USER_FACE0 = "https://s2.loli.net/2023/03/15/c5pfwhgxUSdiIb4.jpg";
    private static final String USER_FACE1 = "https://s2.loli.net/2023/03/15/YQLtI91x5bpSWgn.jpg";
    private static final String USER_FACE2 = "https://s2.loli.net/2023/03/15/Z5xfWJ1N7siwXcL.jpg";

    @Override
    public AppUser queryMobileIsExist(String mobile) {

        Example userExample = new Example(AppUser.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("mobile", mobile);
        AppUser user = appUserMapper.selectOneByExample(userExample);

        return user;
    }

    @Transactional
    @Override
    public AppUser createUser(String mobile) {

        /**
         * 互联网项目都要考虑可扩展性
         * 如果未来的业务激增, 那么就要分库分表
         * 那么数据库表的主键 id 必须保证全局(全库)唯一, 不得重复
         */
        String userId = sid.nextShort();

        AppUser user = new AppUser();

        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户: " + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE0);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);

        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        appUserMapper.insert(user);

        return user;
    }

    @Override
    public AppUser getUser(String userId) {
        return appUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {

        String userId = updateUserInfoBO.getId();

        AppUser userInfo = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, userInfo);

        userInfo.setUpdatedTime(new Date());
        userInfo.setActiveStatus(UserStatus.ACTIVE.type);

        int result = appUserMapper.updateByPrimaryKeySelective(userInfo);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

        // 再次查询用户的最新信息, 放入 redis 中
        AppUser user = getUser(userId);
        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));

    }
}
