package com.zhou.user.service;

import com.zhou.pojo.AppUser;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: yuhang
 * @Date: 2023/3/15 20:39
 * @Version: 1.0
 **/
public interface UserService {

    /**
     * 判断用户是否存在, 如果存在, 返回 user 信息
     * @param mobile
     * @return
     */
    public AppUser queryMobileIsExist(String mobile);

    /**
     * 创建用户, 新增用户记录到数据库
     * @param mobile
     * @return
     */
    public AppUser createUser(String mobile);
}
