package com.cms.admin.security;

import com.cms.common.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;

    public boolean isLocked(String username) {
        String lockKey = CommonConstant.LOGIN_LOCK_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    public void incrementFail(String username) {
        String key = CommonConstant.LOGIN_LOCK_PREFIX + username + ":fail";
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count >= CommonConstant.LOGIN_MAX_RETRY) {
            redisTemplate.opsForValue().set(CommonConstant.LOGIN_LOCK_PREFIX + username, "locked",
                    CommonConstant.LOGIN_LOCK_TIME, TimeUnit.SECONDS);
            redisTemplate.delete(key);
        } else {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
    }

    public void clearFail(String username) {
        String key = CommonConstant.LOGIN_LOCK_PREFIX + username + ":fail";
        redisTemplate.delete(key);
    }
}
