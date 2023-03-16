package com.zhou.user.service;

import com.zhou.enums.Sex;
import com.zhou.enums.UserStatus;
import com.zhou.pojo.AppUser;
import com.zhou.user.mapper.AppUserMapper;
import com.zhou.utils.DateUtil;
import com.zhou.utils.DesensitizationUtil;
import org.n3r.idworker.Sid;
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
}
