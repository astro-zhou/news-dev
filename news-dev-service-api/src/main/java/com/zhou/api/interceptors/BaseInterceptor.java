package com.zhou.api.interceptors;

import com.zhou.exception.GraceException;
import com.zhou.grace.result.ResponseStatusEnum;
import com.zhou.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: yuhang
 * @Date: 2023/3/24 16:17
 * @Version: 1.0
 **/
@Component
public class BaseInterceptor {

    @Autowired
    public RedisOperator redis;
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";

    public boolean verifyUserIdToken(String id, String token, String redisKeyPrefix) {

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(token)) {

            String redisToken = redis.get(redisKeyPrefix + ":" + id);

            if (StringUtils.isBlank(id)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            } else {
                if (!redisToken.equalsIgnoreCase(token)) {
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        } else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        return true;
    }

}
