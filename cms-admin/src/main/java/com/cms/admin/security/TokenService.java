package com.cms.admin.security;

import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.common.constant.CommonConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Token 服务：登录令牌的生成、验证、销毁
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    private final SysUserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成登录 token 并存入 Redis
     *
     * @param user 用户信息
     * @return token 字符串
     */
    public String createToken(SysUser user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = CommonConstant.TOKEN_CACHE_PREFIX + token;

        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(user),
                    CommonConstant.TOKEN_EXPIRE, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Token 存储失败", e);
        }
        return token;
    }

    /**
     * 根据 token 获取用户信息（自动续期）
     *
     * @param token 令牌
     * @return 用户信息，token 无效返回 null
     */
    public SysUser getUserByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String key = CommonConstant.TOKEN_CACHE_PREFIX + token;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        // 滑动续期
        redisTemplate.expire(key, CommonConstant.TOKEN_EXPIRE, TimeUnit.SECONDS);

        try {
            return objectMapper.readValue(json, SysUser.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 header 中解析 Bearer token
     *
     * @param authHeader Authorization header 值
     * @return token 字符串
     */
    public String resolveToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 删除 token（登出）
     *
     * @param token 令牌
     */
    public void removeToken(String token) {
        if (token != null && !token.isEmpty()) {
            redisTemplate.delete(CommonConstant.TOKEN_CACHE_PREFIX + token);
        }
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
