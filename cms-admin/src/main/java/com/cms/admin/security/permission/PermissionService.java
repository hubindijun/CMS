package com.cms.admin.security.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import java.util.Collection;

/**
 * 权限校验服务，供 @PreAuthorize("@pms.hasPermission('xxx')") 使用
 */
@Service("pms")
public class PermissionService {

    /**
     * 校验当前用户是否拥有指定权限（支持 * 通配符匹配）
     *
     * @param permission 权限标识
     * @return 是否拥有权限
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(perm -> "*:*:*".equals(perm) || PatternMatchUtils.simpleMatch(perm, permission));
    }

    /**
     * 校验当前用户是否拥有任意一个指定权限
     *
     * @param permissions 权限标识数组
     * @return 是否拥有任一权限
     */
    public boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
