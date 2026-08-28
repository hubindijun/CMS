package com.cms.admin.security.permission;

import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.entity.SysUser;
import com.cms.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 权限缓存服务：用户权限URL列表的Redis缓存管理与匹配
 */
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final StringRedisTemplate redisTemplate;
    private final SysPermissionMapper permissionMapper;
    private final SysUserMapper userMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 获取用户资源路径权限列表，优先从缓存读取
     *
     * @param username 用户名
     * @return 资源路径规则列表
     */
    public List<String> getUserPermissions(String username) {
        String key = CommonConstant.PERM_CACHE_PREFIX + username;
        List<String> cached = redisTemplate.opsForList().range(key, 0, -1);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            return List.of();
        }

        List<String> paths = permissionMapper.selectResourcePathsByUserId(user.getId());
        if (paths != null && !paths.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, paths.toArray(new String[0]));
            redisTemplate.expire(key, CommonConstant.PERM_CACHE_EXPIRE, TimeUnit.SECONDS);
        }
        return paths != null ? paths : List.of();
    }

    /**
     * 判断请求URI是否匹配用户资源路径权限
     *
     * @param requestUri 请求URI
     * @param username 用户名
     * @return 是否有权限
     */
    public boolean hasPermission(String requestUri, String username) {
        List<String> paths = getUserPermissions(username);
        for (String pattern : paths) {
            if (antPathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清除指定用户的权限缓存
     *
     * @param username 用户名
     */
    public void clearUserCache(String username) {
        redisTemplate.delete(CommonConstant.PERM_CACHE_PREFIX + username);
    }

    /**
     * 清除所有用户的权限缓存（权限规则变更时调用）
     */
    public void clearAllCache() {
        var keys = redisTemplate.keys(CommonConstant.PERM_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
