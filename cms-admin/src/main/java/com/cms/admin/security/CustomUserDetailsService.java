package com.cms.admin.security;

import com.cms.admin.entity.SysPermission;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;

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
            throw new BusinessException("账号已禁用");
        }

        List<Long> roleIds = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cms.admin.entity.SysUserRole>()
                        .eq(com.cms.admin.entity.SysUserRole::getUserId, user.getId())
        ).stream().map(com.cms.admin.entity.SysUserRole::getRoleId).collect(Collectors.toList());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

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
