package com.cms.admin.security;

import com.cms.admin.entity.SysPermission;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.common.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务，根据用户名加载用户信息和权限列表
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    /**
     * 根据用户名加载用户详情（包含权限标识集合）。
     * 超级管理员（root角色）直接授予 *:*:* 通配符权限。
     *
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new DisabledException("账号已禁用");
        }

        List<Long> roleIds = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cms.admin.entity.SysUserRole>()
                        .eq(com.cms.admin.entity.SysUserRole::getUserId, user.getId())
        ).stream().map(com.cms.admin.entity.SysUserRole::getRoleId).collect(Collectors.toList());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (roleIds.isEmpty()) {
            return new User(user.getUsername(), user.getPassword(), authorities);
        }

        boolean isRoot = roleMapper.selectBatchIds(roleIds).stream()
                .anyMatch(r -> CommonConstant.ROOT_ROLE_CODE.equals(r.getCode()));

        if (isRoot) {
            authorities.add(new SimpleGrantedAuthority("*:*:*"));
        } else {
            List<SysPermission> permissions = permissionMapper.selectByUserId(user.getId());
            for (SysPermission perm : permissions) {
                if (perm.getPerms() != null && !perm.getPerms().isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(perm.getPerms()));
                }
            }
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
