package com.cms.admin.controller;

import com.cms.admin.entity.SysPermission;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.vo.UserInfoVO;
import com.cms.common.base.Result;
import com.cms.common.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证相关接口
 * <p>
 * 提供当前登录用户信息查询接口，包含用户基本信息、角色列表与权限标识。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息（基本信息 + 角色 + 权限）
     */
    @GetMapping("/user-info")
    public Result<UserInfoVO> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        List<Long> roleIds = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cms.admin.entity.SysUserRole>()
                        .eq(com.cms.admin.entity.SysUserRole::getUserId, user.getId())
        ).stream().map(com.cms.admin.entity.SysUserRole::getRoleId).collect(Collectors.toList());

        List<String> roleCodes;
        boolean isRoot = false;
        if (roleIds.isEmpty()) {
            roleCodes = List.of();
        } else {
            roleCodes = roleMapper.selectBatchIds(roleIds).stream()
                    .map(SysRole::getCode).collect(Collectors.toList());
            isRoot = roleCodes.contains(CommonConstant.ROOT_ROLE_CODE);
        }
        List<String> permissions;
        if (isRoot) {
            permissions = List.of("*:*:*");
        } else {
            permissions = permissionMapper.selectByUserId(user.getId()).stream()
                    .map(SysPermission::getPerms)
                    .filter(p -> p != null && !p.isEmpty())
                    .collect(Collectors.toList());
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(roleCodes);
        vo.setPermissions(permissions);

        return Result.ok(vo);
    }
}
