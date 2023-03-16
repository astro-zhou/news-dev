package com.zhou.user.mapper;

import com.zhou.my.mapper.MyMapper;
import com.zhou.pojo.AppUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMapper extends MyMapper<AppUser> {
}