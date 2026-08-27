package com.cms.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.base.Result;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 菜单接口
 * <p>
 * 提供当前登录用户的菜单树查询接口，用于前端动态路由渲染。
 * </p>
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysPermissionService permissionService;
    private final SysUserMapper userMapper;

    /**
     * 获取当前登录用户的菜单树
     *
     * @return 菜单树结构列表
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> menuTree() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.ok(permissionService.menuTreeByUserId(user.getId()));
    }
}
