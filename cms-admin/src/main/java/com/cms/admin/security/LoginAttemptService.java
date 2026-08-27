package com.cms.admin.security;

import com.cms.common.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * 登录失败计数与锁定服务，基于 Redis 实现
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 检查账号是否已被锁定
     *
     * @param username 用户名
     * @return 是否锁定
     */
    public boolean isLocked(String username) {
        String lockKey = CommonConstant.LOGIN_LOCK_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * 增加登录失败次数，达到阈值后锁定账号
     *
     * @param username 用户名
     */
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

    /**
     * 清除登录失败计数（登录成功时调用）
     *
     * @param username 用户名
     */
    public void clearFail(String username) {
        String key = CommonConstant.LOGIN_LOCK_PREFIX + username + ":fail";
        redisTemplate.delete(key);
    }
}
